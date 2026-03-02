package com.example.edutrack.repository

import com.example.edutrack.model.CourseModel
import com.example.edutrack.model.EnrollmentModel
import com.example.edutrack.model.LessonModel
import com.example.edutrack.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl: UserRepo {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref : DatabaseReference = database.getReference("Users")

    val courseRef = database.getReference("Courses")
    val enrollRef = database.getReference("Enrollments")
    val lessonRef = database.getReference("Lessons")
    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    callback(true,"Login success")
                }else{
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    auth.currentUser?.uid ?: ""
                    callback(true,"Registration success","${auth.currentUser?.uid}")
                }else{
                    callback(false,"${it.exception?.message}","")
                }
            }
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"User registered successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun getUserById(userId: String, callback: (Boolean, UserModel?) -> Unit) {
        ref.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                callback(user != null, user)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, null)
            }
        })
    }

    override fun getAllUser(callback: (Boolean, List<UserModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val allUsers = mutableListOf<UserModel>()
                    for (user in snapshot.children){
                        val model =user.getValue(UserModel:: class.java)
                        if (model != null){
                            allUsers.add(model)
                        }
                    }
                    callback(true,allUsers)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null)            }
        })

    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun deleteUser(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"User deleted successful")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }



    override fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"Profile updated successfully")
            }else{
                callback(false,it.exception?.message ?: "Failed to update profile")
            }
        }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    callback(true,"Reset email sent to$email")
                }else{
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getAllCourses(callback: (Boolean, List<CourseModel>?, String) -> Unit) {
        courseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courses = mutableListOf<CourseModel>()
                for (child in snapshot.children) {
                    val model = child.getValue(CourseModel::class.java)
                    if (model != null) courses.add(model)
                }
                callback(true, courses, "Success")
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, null, error.message)
            }
        })
    }

    override fun addCourse(course: CourseModel, callback: (Boolean, String) -> Unit) {
        val id = course.courseId.ifEmpty { courseRef.push().key ?: "" }
        val finalCourse = course.copy(courseId = id)

        courseRef.child(id).setValue(finalCourse).addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "Course Saved")
            else callback(false, task.exception?.message ?: "Error saving course")
        }
    }

    override fun deleteCourse(
        courseId: String,
        callback: (Boolean, String) -> Unit
    ) {
        courseRef.child(courseId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Course deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Failed to delete course")
            }
        }
    }

    override fun enrollInCourse(enrollment: EnrollmentModel, callback: (Boolean, String) -> Unit) {
        val id = enrollRef.push().key ?: ""
        val finalEnroll = enrollment.copy(enrollmentId = id)

        enrollRef.child(id).setValue(finalEnroll).addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "Enrolled Successfully")
            else callback(false, task.exception?.message ?: "Error")
        }
    }
    override fun getMyCourses(userId: String, callback: (Boolean, List<EnrollmentModel>?, String) -> Unit) {
        enrollRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val enrollList = mutableListOf<EnrollmentModel>()
                    for (child in snapshot.children) {
                        val model = child.getValue(EnrollmentModel::class.java)
                        if (model != null) {
                            enrollList.add(model)
                        }
                    }
                    callback(true, enrollList, "Success")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, null, error.message)
                }
            })
    }
    override fun getAllEnrollments(callback: (Boolean, List<EnrollmentModel>?, String) -> Unit) {
        enrollRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<EnrollmentModel>()
                for (child in snapshot.children) {
                    val model = child.getValue(EnrollmentModel::class.java)
                    if (model != null) list.add(model)
                }
                callback(true, list, "Success")
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, null, error.message)
            }
        })
    }


    override fun addLesson(lesson: LessonModel, callback: (Boolean, String) -> Unit) {
        val id = lesson.lessonId.ifEmpty { lessonRef.push().key ?: "" }
        val finalLesson = lesson.copy(lessonId = id)

        lessonRef.child(id).setValue(finalLesson).addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "Lesson Saved")
            else callback(false, task.exception?.message ?: "Error saving lesson")
        }
    }

    override fun getLessonsByCourse(courseId: String, callback: (Boolean, List<LessonModel>?, String) -> Unit) {
        lessonRef.orderByChild("courseId").equalTo(courseId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lessons = mutableListOf<LessonModel>()
                    for (child in snapshot.children) {
                        val model = child.getValue(LessonModel::class.java)
                        if (model != null) lessons.add(model)
                    }
                    callback(true, lessons.sortedBy { it.order }, "Success")
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(false, null, error.message)
                }
            })
    }

    override fun deleteLesson(lessonId: String, callback: (Boolean, String) -> Unit) {
        lessonRef.child(lessonId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Lesson Deleted")
            else callback(false, it.exception?.message ?: "Delete Failed")
        }
    }
}