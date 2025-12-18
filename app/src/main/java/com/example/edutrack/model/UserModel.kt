package com.example.edutrack.model

data class UserModel(
    val userId : String = "",
    val email : String = "",
    val firstName : String = "",
    val lastName : String = "",
    val contact : String = "",
    val dob : String = "",
    val gender : String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf()
    }
}
