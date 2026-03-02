package com.example.edutrack.model

data class CourseModel(
    val courseId: String = "",
    val courseName: String = "",
    val courseCode: String = "",
    val creditHours: String = "",
    val instructorName: String = "",
    val description: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "courseId" to courseId,
            "courseName" to courseName,
            "courseCode" to courseCode,
            "creditHours" to creditHours,
            "instructorName" to instructorName,
            "description" to description
        )
    }
}