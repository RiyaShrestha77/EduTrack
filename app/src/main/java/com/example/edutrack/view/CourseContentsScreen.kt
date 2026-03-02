package com.example.edutrack.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edutrack.R
import com.example.edutrack.model.CourseModel
import com.example.edutrack.model.LessonModel
import com.example.edutrack.ui.theme.Blue
import com.example.edutrack.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseContentsScreen(
    course: CourseModel,
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    val lessons by viewModel.lessons.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val context = LocalContext.current

    val currentUser = viewModel.getCurrentUser()
    val isAdmin = currentUser?.email == "admin@gmail.com"

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingLesson by remember { mutableStateOf<LessonModel?>(null) }
    var viewingLesson by remember { mutableStateOf<LessonModel?>(null) }

    LaunchedEffect(course.courseId) {
        viewModel.getLessons(course.courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(course.courseName, color = Color.White, fontSize = 18.sp)
                        Text(course.courseCode, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blue),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = {
                        editingLesson = null
                        showAddEditDialog = true
                    },
                    containerColor = Blue,
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            if (isLoading && lessons.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Blue)
            } else if (lessons.isEmpty()) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No lessons available.", color = Color.Gray)
                    Text("Course Instructor: ${course.instructorName}", fontSize = 12.sp, color = Color.LightGray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    item {
                        Text("Description", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(course.description, fontSize = 13.sp, color = Color.Gray)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    }

                    items(lessons) { lesson ->
                        LessonItem(
                            lesson = lesson,
                            isAdmin = isAdmin,
                            onLessonClick = { viewingLesson = lesson },
                            onEditClick = {
                                editingLesson = lesson
                                showAddEditDialog = true
                            },
                            onDeleteClick = {
                                viewModel.deleteLesson(lesson.lessonId, course.courseId)
                                Toast.makeText(context, "Lesson Deleted", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddEditDialog) {
        AddOrEditLessonDialog(
            lesson = editingLesson,
            onDismiss = { showAddEditDialog = false },
            onSave = { title, desc ->
                val lessonToSave = if (editingLesson != null) {
                    editingLesson!!.copy(title = title, contentDescription = desc)
                } else {
                    LessonModel(
                        courseId = course.courseId,
                        title = title,
                        contentDescription = desc,
                        order = lessons.size + 1,
                        lessonId = ""
                    )
                }

                viewModel.addLesson(lessonToSave) { success, _ ->
                    if (success) {
                        showAddEditDialog = false
                        editingLesson = null
                        viewModel.getLessons(course.courseId)
                        Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    // Detail View Dialog
    viewingLesson?.let { lesson ->
        AlertDialog(
            onDismissRequest = { viewingLesson = null },
            title = { Text(lesson.title, fontWeight = FontWeight.Bold) },
            text = { Text(lesson.contentDescription) },
            confirmButton = {
                TextButton(onClick = { viewingLesson = null }) { Text("Close") }
            }
        )
    }
}

@Composable
fun LessonItem(
    lesson: LessonModel,
    isAdmin: Boolean,
    onLessonClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onLessonClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = Blue, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(lesson.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(lesson.contentDescription, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
            }
            if (isAdmin) {
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "Edit", tint = Color.Gray)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun AddOrEditLessonDialog(
    lesson: LessonModel?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(lesson?.title ?: "") }
    var desc by remember { mutableStateOf(lesson?.contentDescription ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (lesson == null) "New Lesson" else "Update Lesson") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lesson Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Lesson Content/Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotEmpty()) onSave(title, desc) },
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text(if (lesson == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
