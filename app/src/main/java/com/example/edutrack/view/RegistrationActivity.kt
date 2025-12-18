package com.example.edutrack.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edutrack.model.UserModel
import com.example.edutrack.repository.UserRepoImpl
import com.example.edutrack.ui.theme.PurpleGrey80
import com.example.edutrack.ui.theme.White
import com.example.edutrack.viewmodel.UserViewModel
import java.util.Calendar

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistrationBody()
        }
    }
}

@Composable
fun RegistrationBody() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            selectedDate = "$y/${m + 1}/$d"
        },
        year, month, day
    )

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.Blue
                    )
                )
            }


            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    placeholder = { Text("abc@gmail.com") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = PurpleGrey80,
                        focusedContainerColor = PurpleGrey80,
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(15.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }


            item {
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    enabled = false,
                    placeholder = { Text("dd/mm/yyyy") },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = PurpleGrey80,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .clickable { datePicker.show() },
                    shape = RoundedCornerShape(15.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            /** -------- Password -------- **/
            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("****") },
                    trailingIcon = {
                        IconButton(onClick = { visibility = !visibility }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation =
                        if (!visibility) PasswordVisualTransformation()
                        else VisualTransformation.None,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = PurpleGrey80,
                        focusedContainerColor = PurpleGrey80,
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(15.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }


            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = terms,
                        onCheckedChange = { terms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Blue,
                            checkmarkColor = White
                        )
                    )
                    Text("I agree to terms & Conditions")
                }
            }


            item {
                Button(
                    onClick = {
                        if (!terms) {
                            Toast.makeText(
                                context,
                                "Please agree to terms & conditions",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            userViewModel.register(email, password) { success, message, userId ->
                                if (success) {
                                    val model = UserModel(
                                        userId = userId,
                                        email = email,
                                        firstName = "",
                                        lastName = "",
                                        dob = selectedDate,
                                        gender = ""
                                    )

                                    userViewModel.addUserToDatabase(userId, model) { s, msg ->
                                        Toast.makeText(
                                            context,
                                            msg,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Sign Up")
                }
            }


            item {
                Text(
                    buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(SpanStyle(color = Color.Blue)) {
                            append("Sign In")
                        }
                    },
                    modifier = Modifier
                        .clickable {
                            context.startActivity(
                                Intent(context, LoginActivity::class.java)
                            )
                        }
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
@Preview
@Composable
fun RegistrationPreview() {
    RegistrationBody()
}