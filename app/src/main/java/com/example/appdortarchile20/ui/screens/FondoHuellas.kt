package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.appdortarchile20.R

data class HuellaConfig(
    val xFrac: Float,   // posición horizontal 0..1
    val yFrac: Float,   // posición vertical 0..1
    val rotacion: Float,
    val tamano: Dp = 70.dp
)

val huellasPosiciones = listOf(
    HuellaConfig(0.02f, 0.03f,  -25f, 60.dp),
    HuellaConfig(0.16f, 0.09f,  -10f, 55.dp),
    HuellaConfig(0.78f, 0.02f,   30f, 65.dp),
    HuellaConfig(0.88f, 0.10f,   20f, 50.dp),
    HuellaConfig(0.00f, 0.28f,  -40f, 60.dp),
    HuellaConfig(0.10f, 0.38f,  -25f, 55.dp),
    HuellaConfig(0.84f, 0.27f,   45f, 60.dp),
    HuellaConfig(0.76f, 0.36f,   35f, 50.dp),
    HuellaConfig(0.42f, 0.18f,    5f, 55.dp),
    HuellaConfig(0.54f, 0.25f,   15f, 60.dp),
    HuellaConfig(0.00f, 0.58f,  -20f, 65.dp),
    HuellaConfig(0.12f, 0.66f,  -10f, 55.dp),
    HuellaConfig(0.80f, 0.57f,   40f, 60.dp),
    HuellaConfig(0.88f, 0.65f,   50f, 50.dp),
    HuellaConfig(0.38f, 0.48f,  -15f, 55.dp),
    HuellaConfig(0.52f, 0.55f,   -5f, 60.dp),
    HuellaConfig(0.02f, 0.84f,  -35f, 65.dp),
    HuellaConfig(0.14f, 0.91f,  -20f, 55.dp),
    HuellaConfig(0.76f, 0.83f,   25f, 60.dp),
    HuellaConfig(0.86f, 0.90f,   35f, 55.dp),
    HuellaConfig(0.40f, 0.76f,    5f, 60.dp),
    HuellaConfig(0.56f, 0.83f,   15f, 55.dp),
    HuellaConfig(0.28f, 0.33f,  -10f, 50.dp),
    HuellaConfig(0.66f, 0.44f,   20f, 60.dp),
)

@Composable
fun FondoHuellas(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF0038A5),
    alpha: Float = 0.13f,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Fondo con huellas usando BoxWithConstraints para posicionamiento relativo
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val anchoTotal = maxWidth
            val altoTotal = maxHeight

            huellasPosiciones.forEach { huella ->
                Image(
                    painter = painterResource(id = R.drawable.patita),
                    contentDescription = null,
                    modifier = Modifier
                        .size(huella.tamano)
                        .offset(
                            x = anchoTotal * huella.xFrac,
                            y = altoTotal * huella.yFrac
                        )
                        .rotate(huella.rotacion)
                        .alpha(alpha),
                    contentScale = ContentScale.Fit,
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color)
                )
            }
        }
        // Contenido encima
        content()
    }
}