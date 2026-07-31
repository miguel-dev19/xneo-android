package com.xneo.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is AuthState.Success) {
            navController.navigate("home") { popUpTo("home") { inclusive = true } }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Text("XNEO", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE94560))
            Text("Mira sin limites", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(Color(0xFFE94560).copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color(0xFFE94560), modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Iniciar Sesion en XNEO", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Accede a tu cuenta para disfrutar de todas las funciones", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(28.dp))

                    OutlinedTextField(
                        value = email, onValueChange = { email = it.trim(); emailError = null },
                        label = { Text("Correo electronico") }, placeholder = { Text("tu@email.com") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFFE94560)) },
                        isError = emailError != null, supportingText = emailError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password, onValueChange = { password = it; passwordError = null },
                        label = { Text("Contrasena") }, placeholder = { Text("......") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFE94560)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color.Gray)
                            }
                        },
                        isError = passwordError != null, supportingText = passwordError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Olvidaste tu contrasena?", color = Color(0xFFE94560), fontSize = 14.sp, modifier = Modifier.align(Alignment.End).clickable { })
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            var hasError = false
                            if (email.isBlank()) { emailError = "El correo electronico es requerido"; hasError = true }
                            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "Ingresa un email valido"; hasError = true }
                            if (password.length < 6) { passwordError = "La contrasena debe tener al menos 6 caracteres"; hasError = true }
                            if (!hasError) viewModel.login(email, password)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = loginState !is AuthState.Loading
                    ) {
                        if (loginState is AuthState.Loading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        else Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text("No tienes cuenta? ", color = Color.Gray)
                Text("Registrate aqui", color = Color(0xFFE94560), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.navigate("register") })
            }
        }
    }
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFE94560),
    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
    focusedLabelColor = Color(0xFFE94560),
    unfocusedLabelColor = Color.Gray,
    cursorColor = Color(0xFFE94560),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
