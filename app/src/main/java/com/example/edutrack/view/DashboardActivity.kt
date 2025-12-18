package com.example.edutrack.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.edutrack.R
import com.example.edutrack.ui.theme.LightBg
import com.example.edutrack.ui.theme.NavyBlue
import com.example.edutrack.ui.theme.ProfessionalBlue
import com.example.edutrack.ui.theme.White

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

    var selectedIndex by remember { mutableStateOf(0) }

    data class NavItem(val label: String, val icon: Int)


    val navItems = listOf(
        NavItem("Dashboard", R.drawable.baseline_home_24)

    )

    Scaffold(


        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NavyBlue,
                    titleContentColor = White,
                    navigationIconContentColor = White,
                    actionIconContentColor = White
                ),
                title = {
                    Text(
                        "EduTrack",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            painterResource(R.drawable.baseline_notifications_24),
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        },


        bottomBar = {
            NavigationBar(containerColor = White) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = { Text(item.label) },
                        icon = {
                            Icon(
                                painterResource(item.icon),
                                contentDescription = item.label
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ProfessionalBlue,
                            selectedTextColor = ProfessionalBlue,
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LightBg)
        ) {


            when (selectedIndex) {
                0 -> DashboardScreen()
                1 -> HomeScreen()
                2 -> ProfileScreen()
                else -> DashboardScreen()
            }
        }
    }
}
