package com.example.edutrack.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edutrack.model.CourseModel
import com.example.edutrack.ui.theme.Blue
import com.example.edutrack.ui.theme.White
import com.example.edutrack.viewmodel.UserViewModel

@Composable
fun AddCourseScreen(
    viewModel: UserViewModel,
    onBack: () -> Unit,
    existingCourse: CourseModel? = null
) {
    var name by remember { mutableStateOf(existingCourse?.courseName ?: "") }
    var code by remember { mutableStateOf(existingCourse?.courseCode ?: "") }
    var credits by remember { mutableStateOf(existingCourse?.creditHours ?: "") }
    var instructor by remember { mutableStateOf(existingCourse?.instructorName ?: "") }
    var desc by remember { mutableStateOf(existingCourse?.description ?: "") }
    
    val isLoading by viewModel.isLoading.observeAsState(false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F6)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (existingCourse == null) "Create New Course" else "Update Course",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth().testTag("courseName"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = code, onValueChange = { code = it },
                    label = { Text("Course Code") },
                    modifier = Modifier.fillMaxWidth().testTag("courseCode"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = credits, onValueChange = { credits = it },
                    label = { Text("Credit Hours") },
                    modifier = Modifier.fillMaxWidth().testTag("creditHours"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = instructor, onValueChange = { instructor = it },
                    label = { Text("Instructor Name") },
                    modifier = Modifier.fillMaxWidth().testTag("instructorName"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = desc, onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().testTag("courseDesc"),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        val courseToSave = CourseModel(
                            courseId = existingCourse?.courseId ?: "",
                            courseName = name,
                            courseCode = code,
                            creditHours = credits,
                            instructorName = instructor,
                            description = desc
                        )
                        viewModel.addCourse(courseToSave) { success, _ ->
                            if (success) onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("publishCourse"),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (existingCourse == null) "Publish Course" else "Save Changes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TextButton(onClick = onBack, modifier = Modifier.testTag("cancelButton")) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }
}
