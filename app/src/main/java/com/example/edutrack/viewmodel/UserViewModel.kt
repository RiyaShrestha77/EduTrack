package com.example.edutrack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.edutrack.model.CourseModel
import com.example.edutrack.model.EnrollmentModel
import com.example.edutrack.model.LessonModel
import com.example.edutrack.model.UserModel
import com.example.edutrack.repository.UserRepo
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo: UserRepo) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _users = MutableLiveData<UserModel?>()
    val users: LiveData<UserModel?> get() = _users

    private val _courses = MutableLiveData<List<CourseModel>?>()
    val courses: LiveData<List<CourseModel>?> get() = _courses
    private val _myCourses = MutableLiveData<List<EnrollmentModel>?>()
    val myCourses: LiveData<List<EnrollmentModel>?> get() = _myCourses

    private val _lessons = MutableLiveData<List<LessonModel>>()
    val lessons: LiveData<List<LessonModel>> = _lessons
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.login(email, password) { success, msg ->
            _isLoading.value = false
            callback(success, msg)
        }
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        _isLoading.value = true
        repo.register(email, password) { success, msg, uid ->
            _isLoading.value = false
            callback(success, msg, uid)
        }
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.addUserToDatabase(userId, model) { success, msg ->
            _isLoading.value = false
            callback(success, msg)
        }
    }

    fun getUserById(userId: String) {
        repo.getUserById(userId) { success, user ->
            if (success) _users.postValue(user)
        }
    }

    fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.updateProfile(userId, model) { success, msg ->
            _isLoading.value = false
            callback(success, msg)
        }
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.forgetPassword(email) { success, msg ->
            _isLoading.value = false
            callback(success, msg)
        }
    }

    fun getCurrentUser(): FirebaseUser? = repo.getCurrentUser()
    fun logout() = repo.logout()

    fun getAllCourses() {
        repo.getAllCourses { success, list, _ ->
            if (success) _courses.postValue(list)
        }
    }

    fun addCourse(course: CourseModel, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.addCourse(course) { success, msg ->
            _isLoading.value = false
            if (success) {
                getAllCourses()
            }
            callback(success, msg)
        }
    }

    fun deleteCourse(courseId: String) {
        repo.deleteCourse(courseId) { success, _ ->
            if (success) {
                getAllCourses()
            }
        }
    }

    fun addOrUpdateCourse(course: CourseModel, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.addCourse(course) { success, msg ->
            _isLoading.value = false
            if (success) {
                getAllCourses()
            }
            callback(success, msg)
        }
    }


    fun getMyCourses() {
        val currentUserId = repo.getCurrentUser()?.uid ?: ""
        repo.getMyCourses(currentUserId) { success, list, _ ->
            if (success) {
                _myCourses.postValue(list)
            }
        }
    }

    fun enrollInCourse(course: CourseModel, callback: (Boolean, String) -> Unit) {
        val userId = repo.getCurrentUser()?.uid ?: ""
        _isLoading.value = true

        repo.getUserById(userId) { success, userModel ->
            if (success && userModel != null) {
                val enrollment = EnrollmentModel(
                    userId = userId,
                    name = userModel.firstName,
                    courseId = course.courseId,
                    courseName = course.courseName,
                    description = course.description
                )

                repo.enrollInCourse(enrollment) { enrollSuccess, msg ->
                    _isLoading.postValue(false)
                    callback(enrollSuccess, msg)
                }
            } else {
                _isLoading.postValue(false)
                callback(false, "Error: Could not find user profile.")
            }
        }
    }

    fun fetchEnrollmentsForAdmin() {
        repo.getAllEnrollments { success, list, _ ->
            if (success) _myCourses.postValue(list)
        }
    }

    fun getLessons(courseId: String) {
        _isLoading.value = true
        repo.getLessonsByCourse(courseId) { success, list, _ ->
            _isLoading.postValue(false)
            if (success) {
                _lessons.postValue(list ?: emptyList())
            }
        }
    }

    fun addLesson(lesson: LessonModel, onResult: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.addLesson(lesson) { success, message ->
            _isLoading.postValue(false)
            onResult(success, message)
        }
    }
    fun deleteLesson(lessonId: String, courseId: String) {
        _isLoading.value = true
        repo.deleteLesson(lessonId) { success, _ ->
            _isLoading.postValue(false)
            if (success) {
                getLessons(courseId)
            }
        }
    }
}