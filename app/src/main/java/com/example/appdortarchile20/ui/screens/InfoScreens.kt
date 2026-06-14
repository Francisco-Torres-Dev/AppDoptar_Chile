package com.example.appdortarchile20.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// --- PANTALLA NOSOTROS ---
@Composable
fun NosotrosScreen() {
    FondoHuellas {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nuestra Misión",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Appdoptar Chile es una plataforma dedicada a conectar animales rescatados con familias responsables en todo el territorio nacional.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Text(
                text = "Creemos firmemente en la tenencia responsable y en que cada mascota merece una segunda oportunidad. Trabajamos para facilitar el proceso de adopción y dar visibilidad a quienes no tienen voz.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Valores
            Text(
                "Nuestros valores",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            listOf(
                "Amor animal" to "Cada mascota merece cuidado y una familia que la quiera.",
                "Tenencia responsable" to "Promovemos adopción consciente y comprometida.",
                "Comunidad" to "Conectamos personas que comparten el amor por los animales."
            ).forEach { (titulo, desc) ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(titulo, style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        Text(desc, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
    }
}

// --- PANTALLA BLOG ---
@Composable
fun BlogScreen() {
    FondoHuellas {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Consejos y Artículos",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Todo lo que necesitas saber para ser un tutor responsable.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            BlogItem(
                titulo = "10 tips para el primer día",
                contenido = "Prepara un espacio tranquilo, deja que explore a su ritmo y mantén la calma. La paciencia es clave en los primeros días.",
                tag = "Adopción"
            )
            BlogItem(
                titulo = "Importancia de la vacunación",
                contenido = "Mantener el calendario al día protege a tu mascota de enfermedades graves como el parvovirus y el moquillo.",
                tag = "Salud"
            )
            BlogItem(
                titulo = "Ley de tenencia responsable",
                contenido = "Conoce tus deberes como tutor: registro, cuidados de salud y seguridad en espacios públicos. Es obligación en Chile.",
                tag = "Legal"
            )
            BlogItem(
                titulo = "Alimentación según etapa de vida",
                contenido = "Cachorro, adulto o senior: cada etapa requiere nutrientes distintos. Consulta siempre con un veterinario.",
                tag = "Nutrición"
            )
        }
    }
}

@Composable
fun BlogItem(titulo: String, contenido: String, tag: String, textoCompleto: String = "") {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (expandido) 6.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = contenido,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Contenido expandido con animación
            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically() +
                        fadeIn(),
                exit = shrinkVertically() +
                        fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (textoCompleto.isNotEmpty()) textoCompleto
                        else "Este tema es fundamental para el bienestar animal. Te recomendamos consultar con un veterinario de confianza y mantenerte informado sobre las últimas recomendaciones. La comunidad AppDoptar Chile está aquí para ayudarte en cada paso del proceso.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TextButton(
                onClick = { expandido = !expandido },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    if (expandido) "Ver menos" else "Leer más",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    if (expandido) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// --- PANTALLA CONTACTO ---
@Composable
fun ContactoScreen() {
    var nombre by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }
    var enviando by remember { mutableStateOf(false) }

    LaunchedEffect(enviando) {
        if (enviando) {
            kotlinx.coroutines.delay(2000)
            enviando = false
            enviado = true
        }
    }

    FondoHuellas {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Contáctanos",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "Si eres un refugio, tienes dudas o quieres colaborar, escríbenos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Tu nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Mensaje o sugerencia") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(14.dp),
                maxLines = 6
            )

            if (enviado) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "¡Mensaje enviado! Te responderemos pronto.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Button(
                onClick = { if (!enviado && !enviando) enviando = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = nombre.isNotEmpty() && mensaje.isNotEmpty() && !enviando && !enviado
            ) {
                if (enviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Enviando...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar mensaje", fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Info de contacto
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "contacto@appdoptarchile.cl",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "@appdoptarchile",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}