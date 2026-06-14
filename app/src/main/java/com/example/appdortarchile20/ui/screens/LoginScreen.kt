package com.example.appdortarchile20.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appdortarchile20.R
import com.example.appdortarchile20.ui.viewmodel.LoginState
import com.example.appdortarchile20.ui.viewmodel.PetViewModel

@Composable
fun LoginScreen(
    viewModel: PetViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    val naranjaPrincipal = Color(0xFF0038A5)
    val naranjaClaro     = Color(0xFF1A4FBF)
    val crema            = Color(0xFFD0DEFF)

    // Animación de entrada del logo (bounce)
    var logoVisible by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "LogoBounce"
    )

    // Animación de entrada del formulario (fade + slide)
    val formAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 400),
        label = "FormFade"
    )
    val formOffset by animateFloatAsState(
        targetValue = if (logoVisible) 0f else 60f,
        animationSpec = tween(durationMillis = 600, delayMillis = 400),
        label = "FormSlide"
    )

    LaunchedEffect(Unit) { logoVisible = true }
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) onLoginSuccess()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo gradiente azul
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = listOf(naranjaPrincipal, naranjaClaro)))
        )
        // Huellas encima del gradiente en blanco semitransparente
        FondoHuellas(alpha = 0.12f, color = Color.White) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo con bounce
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "AppDoptar Chile",
                    modifier = Modifier
                        .width(260.dp)
                        .height(210.dp)
                        .scale(logoScale)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Nombre tricolor
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.scale(logoScale)
                ) {
                    Text("App",    fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Doptar", fontSize = 32.sp, fontWeight = FontWeight.Black, color = crema)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chile",  fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF398E3D))
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Eslogan en cursiva
                Text(
                    text = "\"Cada mascota merece una segunda oportunidad\"",
                    color = Color.White.copy(alpha = 0.92f * formAlpha),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Formulario con fade + slide
                Box(modifier = Modifier.offset(y = formOffset.dp).graphicsLayer(alpha = formAlpha)) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                OutlinedTextField(
                                    value = email, onValueChange = { email = it },
                                    label = { Text("Correo Electrónico", color = Color.White) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedTextColor = Color.White, focusedTextColor = Color.White,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                        focusedBorderColor = Color.White, cursorColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = password, onValueChange = { password = it },
                                    label = { Text("Contraseña", color = Color.White) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedTextColor = Color.White, focusedTextColor = Color.White,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                        focusedBorderColor = Color.White, cursorColor = Color.White
                                    )
                                )
                            }
                        }

                        if (loginState is LoginState.Error) {
                            Text(
                                text = (loginState as LoginState.Error).message,
                                color = crema,
                                modifier = Modifier.padding(top = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.login(email, password) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Iniciar Sesión", color = naranjaPrincipal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        TextButton(onClick = onRegisterClick, modifier = Modifier.padding(top = 8.dp).fillMaxWidth()) {
                            Text("¿No tienes cuenta? Regístrate aquí", color = Color.White, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        } // FondoHuellas
    } // Box externo
}