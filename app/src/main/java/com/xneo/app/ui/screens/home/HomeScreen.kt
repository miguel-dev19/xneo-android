package com.xneo.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.xneo.app.ui.components.ShimmerVideoCard
import com.xneo.app.ui.components.VideoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val videos by viewModel.videos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("XNEO", fontWeight = FontWeight.Bold, color = Color(0xFFE94560), fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* Sidebar */ }) {
                        Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate("login") }) {
                        Text("Entrar", color = Color(0xFFE94560))
                    }
                    TextButton(onClick = { navController.navigate("register") }) {
                        Text("Registro", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A2E))
            )
        },
        containerColor = Color(0xFF0F0F1A)
    ) { padding ->
        if (isLoading && videos.isEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp, padding.calculateTopPadding(), 8.dp, 8.dp)
            ) {
                items(10) { ShimmerVideoCard() }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp, padding.calculateTopPadding(), 8.dp, 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(videos) { video ->
                    VideoCard(video = video, onClick = { navController.navigate("player/${video.id}") })
                }
            }
        }
    }
}
