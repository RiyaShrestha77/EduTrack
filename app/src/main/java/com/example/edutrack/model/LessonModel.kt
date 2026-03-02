package com.example.edutrack.model

data class LessonModel(
    val lessonId: String = "",
    val courseId: String = "",
    val title: String = "",
    val contentDescription: String = "",
    val videoUrl: String = "",
    val order: Int = 0
)