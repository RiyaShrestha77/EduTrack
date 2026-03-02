package com.example.edutrack

import com.example.edutrack.model.UserModel
import com.example.edutrack.repository.UserRepo
import com.example.edutrack.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UserViewModelSettingsTest {

    @Test
    fun logout_test() {

        val repo = mock<UserRepo>()

        val viewModel = UserViewModel(repo)


        viewModel.logout()


        verify(repo).logout()
    }

    @Test
    fun update_profile_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)
        val userId = "user123"
        val userModel = UserModel(
            userId = userId,
            firstName = "John",
            lastName = "Doe",
            contact = "1234567890",
            dob = "01/01/2000"
        )


        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Profile Updated Successfully")
            null
        }.`when`(repo).updateProfile(eq(userId), eq(userModel), any())

        var successResult = false
        var messageResult = ""


        viewModel.updateProfile(userId, userModel) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Profile Updated Successfully", messageResult)


        verify(repo).updateProfile(eq(userId), eq(userModel), any())
    }
}
