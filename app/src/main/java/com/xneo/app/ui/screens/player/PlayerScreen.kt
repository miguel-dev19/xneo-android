package com.xneo.app.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.xneo.app.ui.components.formatViews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(videoId: String, navController: NavController, viewModel: PlayerViewModel = viewModel()) {
    val video by viewModel.video.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showLoginModal by remember { mutableStateOf(false) }

    LaunchedEffect(videoId) { viewModel.loadVideo(videoId) }

    if (showLoginModal) {
        AlertDialog(
            onDismissRequest = { showLoginModal = false },
            title = { Text("Contenido Exclusivo XNEO", fontWeight = FontWeight.Bold, color = Color(0xFFE94560)) },
            text = { Text("Crea tu cuenta gratis para dar like, comentar y descargar videos.", color = Color.White) },
            confirmButton = {
                Button(onClick = { showLoginModal = false; navController.navigate("register") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560))) {
                    Text("Crear Cuenta Gratis")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginModal = false }) { Text("Ahora no", color = Color.Gray) }
            },
            containerColor = Color(0xFF1E1E2E)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(video?.title ?: "Cargando...", fontSize = 16.sp, maxLines = 1, color = Color.White) },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A2E))
            )
        },
        containerColor = Color(0xFF0F0F1A)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE94560))
            }
        } else if (video != null) {
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp).background(Color.Black), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(64.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(video!!.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${formatViews(video!!.views)} vistas . ${video!!.uploadDate}", color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("!!", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Estas viendo como invitado", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Crea una cuenta gratis para dar like, comentar y descargar este video.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { navController.navigate("register") }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560))) {
                            Text("Crear Cuenta Gratis")
                        }
                        OutlinedButton(onClick = { navController.navigate("login") }, modifier = Modifier.weight(1f)) {
                            Text("Iniciar Sesion")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf(
                        Triple(Icons.Default.ThumbUp, "Like", "Registrate para dar like"),
                        Triple(Icons.Default.ThumbDown, "Dislike", "Registrate para dar dislike"),
                        Triple(Icons.Default.Comment, "Comentar", "Registrate para comentar"),
                        Triple(Icons.Default.Download, "Descargar", "Registrate para descargar")
                    ).forEach { (icon, label, _) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { showLoginModal = true }) { Icon(icon, label, tint = Color.Gray) }
                            Text(label, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
