package com.example.edutrack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.edutrack.repository.UserRepo
import com.example.edutrack.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any

class UserViewModelLoginTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repo: UserRepo

    private lateinit var viewModel: UserViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = UserViewModel(repo)
    }

    @Test
    fun `login success`() {
        val email = "test@gmail.com"
        val password = "password"
        
        `when`(repo.login(anyString(), anyString(), any())).thenAnswer {
            val callback = it.arguments[2] as (Boolean, String) -> Unit
            callback(true, "Login Successful")
        }

        var successResult = false
        viewModel.login(email, password) { success, _ ->
            successResult = success
        }

        assertEquals(true, successResult)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `login failure`() {
        val email = "test@gmail.com"
        val password = "wrongpassword"

        `when`(repo.login(anyString(), anyString(), any())).thenAnswer {
            val callback = it.arguments[2] as (Boolean, String) -> Unit
            callback(false, "Login failed")
        }

        var successResult = true
        viewModel.login(email, password) { success, _ ->
            successResult = success
        }

        assertEquals(false, successResult)
        assertEquals(false, viewModel.isLoading.value)
    }
}
