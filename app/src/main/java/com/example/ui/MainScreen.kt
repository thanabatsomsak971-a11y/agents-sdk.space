package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AgentConfigScreen
import com.example.ui.screens.AgentSpaceScreen
import com.example.ui.screens.CodePlaygroundScreen
import com.example.ui.screens.FirebaseConsoleScreen
import com.example.ui.theme.AgentCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepDarkBackground
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AgentConfigViewModel
import com.example.ui.viewmodel.AgentSpaceViewModel
import com.example.ui.viewmodel.FirebaseConsoleViewModel

enum class NavItem(
    val title: String,
    val icon: ImageVector,
    val tag: String
) {
    SPACE("Agent Space", Icons.Default.SmartToy, "nav_agent_space"),
    FIREBASE("Firebase", Icons.Default.Dashboard, "nav_firebase_console"),
    CONFIG("SDK Config", Icons.Default.Settings, "nav_sdk_config"),
    CODE("Code", Icons.Default.Code, "nav_code_playground")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    spaceViewModel: AgentSpaceViewModel = viewModel(),
    consoleViewModel: FirebaseConsoleViewModel = viewModel(),
    configViewModel: AgentConfigViewModel = viewModel()
) {
    var currentItem by remember { mutableStateOf(NavItem.SPACE) }
    val config by configViewModel.config.collectAsState()
    val isSpeaking by spaceViewModel.voiceManager.isSpeaking.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agent SDK Space",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🔥 asia-east1",
                            color = FirebaseAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 4.dp
            ) {
                NavItem.values().forEach { item ->
                    val selected = currentItem == item
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentItem = item },
                        icon = {
                            if (item == NavItem.SPACE && isSpeaking) {
                                BadgedBox(badge = {
                                    Badge(
                                        containerColor = AgentCyan,
                                        modifier = Modifier.size(6.dp)
                                    )
                                }) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = FirebaseAmber,
                            indicatorColor = FirebaseAmber,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        },
        containerColor = DeepDarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentItem) {
                NavItem.SPACE -> AgentSpaceScreen(
                    viewModel = spaceViewModel,
                    config = config
                )

                NavItem.FIREBASE -> FirebaseConsoleScreen(
                    viewModel = consoleViewModel
                )

                NavItem.CONFIG -> AgentConfigScreen(
                    configViewModel = configViewModel,
                    spaceViewModel = spaceViewModel
                )

                NavItem.CODE -> CodePlaygroundScreen(
                    configViewModel = configViewModel,
                    spaceViewModel = spaceViewModel
                )
            }
        }
    }
}
