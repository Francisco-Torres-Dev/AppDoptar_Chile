package com.example.appdortarchile20.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.appdortarchile20.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val naranjaPrincipal = Color(0xFF0038A5)
    val naranjaClaro     = Color(0xFF1A4FBF)
    val verdeSalvia      = Color(0xFF4A7C59)
    val crema            = Color(0xFFD0DEFF)

    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 0.8f,
        animationSpec = tween(durationMillis = 1000),
        label = "LogoScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onTimeout()
    }

    FondoHuellas(alpha = 0.15f, color = Color.White) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = listOf(naranjaPrincipal, naranjaClaro))),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.scale(scale)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "AppDoptar Chile",
                    modifier = Modifier
                        .width(280.dp)
                        .height(272.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Nombre tricolor
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "App",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Doptar",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFF5EB)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Chile",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF398E3D)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"Cada mascota merece una segunda oportunidad\"",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 15.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            CircularProgressIndicator(
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp).size(30.dp)
            )
        }
    }
}