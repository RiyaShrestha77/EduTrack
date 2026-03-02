package com.example.edutrack.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edutrack.model.UserModel
import com.example.edutrack.repository.UserRepoImpl
import com.example.edutrack.ui.theme.Blue
import com.example.edutrack.ui.theme.White
import com.example.edutrack.viewmodel.UserViewModel

@Composable
fun ProfileScreen() {
    val viewModel: UserViewModel = remember { UserViewModel(UserRepoImpl()) }
    val firebaseUser = viewModel.getCurrentUser()
    val userState by viewModel.users.observeAsState()

    LaunchedEffect(firebaseUser?.uid) {
        firebaseUser?.uid?.let { viewModel.getUserById(it) }
    }

    val user = userState

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Blue)
        }
    } else {
        ProfileUI(user = user, viewModel = viewModel)
    }
}

@Composable
fun ProfileUI(user: UserModel, viewModel: UserViewModel) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    var isEditing by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var phone by remember { mutableStateOf(user.contact) }
    var dob by remember { mutableStateOf(user.dob) }

    LaunchedEffect(user) {
        firstName = user.firstName
        lastName = user.lastName
        phone = user.contact
        dob = user.dob
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFE))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Blue),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(40.dp),
                    color = White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(48.dp), tint = White)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = user.email, color = White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            if (isEditing) {
                ProfileTextField(firstName, { firstName = it }, "First Name", Icons.Default.Person)
                ProfileTextField(lastName, { lastName = it }, "Last Name", Icons.Default.Person)
                ProfileTextField(phone, { phone = it }, "Phone", Icons.Default.Phone)
                ProfileTextField(dob, { dob = it }, "Birthday", Icons.Default.DateRange)
            } else {
                InfoRow(Icons.Default.Person, "First Name", user.firstName)
                InfoRow(Icons.Default.Person, "Last Name", user.lastName)
                InfoRow(Icons.Default.Email, "Email", user.email)
                InfoRow(Icons.Default.Phone, "Phone", user.contact)
                InfoRow(Icons.Default.DateRange, "Birthday", user.dob)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isEditing) {
                        val updated = user.copy(
                            firstName = firstName,
                            lastName = lastName,
                            contact = phone,
                            dob = dob
                        )
                        viewModel.updateProfile(user.userId, updated) { success, _ ->
                            if (success) {
                                isEditing = false
                                viewModel.getUserById(user.userId)
                            }
                        }
                    } else {
                        isEditing = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isEditing) Color(0xFF4CAF50) else Blue),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(20.dp))
                } else {
                    Text(if (isEditing) "Save Changes" else "Edit Profile", fontWeight = FontWeight.Bold)
                }
            }
            
            if (isEditing) {
                TextButton(
                    onClick = { isEditing = false },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(8.dp),
            color = Blue.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
