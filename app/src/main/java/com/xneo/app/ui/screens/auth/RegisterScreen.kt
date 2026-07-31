package com.xneo.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var acceptedTerms by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf(false) }
    val registerState by viewModel.registerState.collectAsState()
    val usernameState by viewModel.usernameAvailable.collectAsState()

    LaunchedEffect(username) {
        if (username.length >= 3) { delay(500); viewModel.checkUsername(username) }
    }
    LaunchedEffect(registerState) {
        if (registerState is AuthState.Success) {
            navController.navigate("home") { popUpTo("home") { inclusive = true } }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))))) {
        IconButton(onClick = { navController.navigateUp() }, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
            Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
        }
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(top = 80.dp, bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Crear Cuenta en XNEO", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Unete gratis y accede a todas las funciones", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(32.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally).size(70.dp).background(Color(0xFFE94560).copy(alpha = 0.15f), RoundedCornerShape(35.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PersonAdd, null, tint = Color(0xFFE94560), modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username, onValueChange = { username = it.lowercase().filter { c -> c.isLetterOrDigit() || c == '_' }; usernameError = null },
                        label = { Text("Nombre de usuario") }, placeholder = { Text("usuario123") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFFE94560)) },
                        trailingIcon = {
                            when (usernameState) {
                                is UsernameState.Checking -> CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFFE94560), strokeWidth = 2.dp)
                                is UsernameState.Available -> Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                is UsernameState.Unavailable -> Icon(Icons.Default.Cancel, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                else -> null
                            }
                        },
                        isError = usernameError != null,
                        supportingText = {
                            when {
                                usernameError != null -> Text(usernameError!!, color = Color.Red, fontSize = 12.sp)
                                usernameState is UsernameState.Available -> Text("Disponible", color = Color(0xFF4CAF50), fontSize = 12.sp)
                                usernameState is UsernameState.Unavailable -> Text("El nombre de usuario ya esta en uso", color = Color.Red, fontSize = 12.sp)
                                usernameState is UsernameState.TooShort -> Text("Minimo 3 caracteres", color = Color.Gray, fontSize = 12.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

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
                        value = password, onValueChange = { password = it; passwordError = null; if (confirmPassword.isNotEmpty()) confirmPasswordError = null },
                        label = { Text("Contrasena") }, placeholder = { Text("Minimo 6 caracteres") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFE94560)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color.Gray)
                            }
                        },
                        isError = passwordError != null, supportingText = passwordError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it; confirmPasswordError = null },
                        label = { Text("Confirmar contrasena") }, placeholder = { Text("Repite tu contrasena") },
                        leadingIcon = { Icon(Icons.Default.LockReset, null, tint = Color(0xFFE94560)) },
                        isError = confirmPasswordError != null, supportingText = confirmPasswordError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it; termsError = false },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFE94560)))
                        Text("Acepto los ", color = Color.Gray, fontSize = 14.sp)
                        Text("Terminos y Condiciones", color = Color(0xFFE94560), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
                    }
                    if (termsError) Text("Debes aceptar los terminos", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            var hasError = false
                            if (username.length < 3) { usernameError = "Minimo 3 caracteres"; hasError = true }
                            if (usernameState is UsernameState.Unavailable) { usernameError = "El nombre de usuario ya esta en uso"; hasError = true }
                            if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "Ingresa un email valido"; hasError = true }
                            if (password.length < 6) { passwordError = "La contrasena debe tener al menos 6 caracteres"; hasError = true }
                            if (password != confirmPassword) { confirmPasswordError = "Las contrasenas no coinciden"; hasError = true }
                            if (!acceptedTerms) { termsError = true; hasError = true }
                            if (!hasError) viewModel.register(username, email, password)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = registerState !is AuthState.Loading
                    ) {
                        if (registerState is AuthState.Loading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        else Text("Crear Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text("Ya tienes cuenta? ", color = Color.Gray)
                Text("Inicia sesion", color = Color(0xFFE94560), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.navigate("login") })
            }
        }
    }
}
