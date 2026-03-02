package com.example.edutrack.view



import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.edutrack.R
import com.example.edutrack.model.CourseModel
import com.example.edutrack.repository.UserRepoImpl
import com.example.edutrack.ui.theme.Blue
import com.example.edutrack.ui.theme.White
import com.example.edutrack.viewmodel.UserViewModel

data class NavItem(val label: String, val icon: Int)

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as Activity

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isViewingNotifications by remember { mutableStateOf(false) }

    var showAddCourse by remember { mutableStateOf(false) }
    var editingCourse by remember { mutableStateOf<CourseModel?>(null) }
    var viewingCourse by remember { mutableStateOf<CourseModel?>(null) }

    if (showAddCourse || editingCourse != null) {
        AddCourseScreen(
            viewModel = userViewModel,
            onBack = {
                showAddCourse = false
                editingCourse = null
            },
            existingCourse = editingCourse
        )
    } else if (viewingCourse != null) {
        CourseContentScreen(
            course = viewingCourse!!,
            viewModel = userViewModel,
            onBack = { viewingCourse = null }
        )
    } else {
        val listNav = listOf(
            NavItem("Home", R.drawable.baseline_home_24),
            NavItem("My Courses", R.drawable.outline_book_2_24),
            NavItem("Profile", R.drawable.outline_person_24),
            NavItem("Settings", R.drawable.outline_settings_24)
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Blue,
                        titleContentColor = White,
                        navigationIconContentColor = White,
                        actionIconContentColor = White
                    ),
                    title = {
                        Text(when {
                            isViewingNotifications -> "Notifications"
                            selectedIndex == 0 -> "LearnNex"
                            selectedIndex == 1 -> "Enrolled Courses"
                            selectedIndex == 2 -> "Profile"
                            else -> "Settings"
                        })
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isViewingNotifications) {
                                isViewingNotifications = false
                            } else {
                                val intent = Intent(activity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                activity.startActivity(intent)
                                activity.finish()
                            }
                        }) {
                            Icon(painterResource(R.drawable.outline_arrow_back_ios_new_24), null)
                        }
                    },
                    actions = {
                        if (!isViewingNotifications) {
                            IconButton(
                                onClick = { isViewingNotifications = true },
                                modifier = Modifier.testTag("top_notification_bell")
                            ) {
                                Icon(Icons.Default.Notifications, null)
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(containerColor = White) {
                    listNav.forEachIndexed { index, item ->
                        NavigationBarItem(
                            modifier = Modifier.testTag("nav_${item.label.lowercase().replace(" ", "")}"),
                            icon = { Icon(painterResource(item.icon), null) },
                            label = { Text(item.label) },
                            onClick = {
                                selectedIndex = index
                                isViewingNotifications = false
                            },
                            selected = !isViewingNotifications && selectedIndex == index
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isViewingNotifications) {
                    NotificationScreen(onBack = { isViewingNotifications = false })
                } else {
                    when (selectedIndex) {
                        0 -> HomeScreen(userViewModel, { showAddCourse = true }, { editingCourse = it }, { viewingCourse = it })
                        1 -> MyCoursesScreen(userViewModel) { viewingCourse = it }
                        2 -> ProfileScreen()
                        3 -> SettingsScreen(
                            onNavigateToProfile = { selectedIndex = 2 },
                            onNavigateToNotifications = { isViewingNotifications = true }
                        )
                    }
                }
            }
        }
    }
}