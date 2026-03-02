package com.example.edutrack.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edutrack.R
import com.example.edutrack.model.CourseModel
import com.example.edutrack.model.EnrollmentModel
import com.example.edutrack.ui.theme.Blue
import com.example.edutrack.viewmodel.UserViewModel

@Composable
fun MyCoursesScreen(viewModel: UserViewModel, onNavigateToContent: (CourseModel) -> Unit) {
    val myEnrolledList by viewModel.myCourses.observeAsState(initial = emptyList())
    var selectedEnrollment by remember { mutableStateOf<EnrollmentModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getMyCourses()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        if (myEnrolledList.isNullOrEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_book_2_24),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "You haven't enrolled in any courses yet.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "My Learning",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(myEnrolledList!!) { enrollment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEnrollment = enrollment },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Blue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_book_2_24),
                                        contentDescription = null,
                                        tint = Blue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    enrollment.courseName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Course ID: ${enrollment.courseId}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        selectedEnrollment?.let { enrollment ->
            AlertDialog(
                onDismissRequest = { selectedEnrollment = null },
                title = {
                    Column {
                        Text(enrollment.courseName, fontWeight = FontWeight.Bold)
                        Text("Course ID: ${enrollment.courseId}", fontSize = 10.sp, color = Color.Gray)
                    }
                },
                text = {
                    Text(
                        if (enrollment.description.isEmpty()) "Continue where you left off in this course."
                        else enrollment.description
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val course = CourseModel(
                                courseId = enrollment.courseId,
                                courseName = enrollment.courseName,
                                description = enrollment.description
                            )
                            onNavigateToContent(course)
                            selectedEnrollment = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Blue)
                    ) {
                        Text("Open Lessons")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedEnrollment = null }) {
                        Text("Back", color = Color.Gray)
                    }
                }
            )
        }
    }
}
