package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

data class Campania(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val metaMonto: Int,
    val montoRecaudado: Int,
    val imagenUrl: String
)

val campaniasMVP = listOf(
    Campania(1, "Cirugía para Luna", "Luna es una perrita atropellada que necesita una operación de urgencia en su pata trasera.", 300_000, 187_000, "https://placedog.net/600/300"),
    Campania(2, "Alimentación Refugio Norte", "El Refugio Esperanza de Antofagasta necesita alimento para sus 45 animales este mes.", 150_000, 98_500, "https://placedog.net/601/300"),
    Campania(3, "Vacunación Colonia Felina", "Campaña para vacunar a 30 gatos callejeros del sector Macul en Santiago.", 80_000, 80_000, "https://placekitten.com/600/300"),
    Campania(4, "Rehabilitación Firulais", "Firulais sobrevivió a un incendio y necesita tratamientos de rehabilitación durante 3 meses.", 500_000, 62_000, "https://placedog.net/602/300")
)

@Composable
fun DonacionesScreen(viewModel: com.example.appdortarchile20.ui.viewmodel.PetViewModel? = null) {
    var campaniaSeleccionada by remember { mutableStateOf<Campania?>(null) }
    val montosRecaudados by (viewModel?.montosRecaudados
        ?: kotlinx.coroutines.flow.MutableStateFlow<Map<Int,Int>>(emptyMap())).collectAsState()

    if (campaniaSeleccionada != null) {
        PasarelaPagoDialog(
            campania = campaniaSeleccionada!!,
            onDismiss = { campaniaSeleccionada = null },
            onPagoExitoso = { monto ->
                val id = campaniaSeleccionada!!.id
                viewModel?.agregarDonacion(id, monto)
            },
            viewModel = viewModel
        )
    }

    FondoHuellas {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text("Campañas de Donación", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Ayuda a financiar el cuidado de animales en situación vulnerable.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            items(campaniasMVP) { campania ->
                val donacionesAdicionales = montosRecaudados[campania.id] ?: 0
                val recaudado = campania.montoRecaudado + donacionesAdicionales
                CampaniaCard(
                    campania = campania.copy(montoRecaudado = recaudado),
                    onDonar = { campaniaSeleccionada = campania }
                )
            }
        }
    } // FondoHuellas
}

@Composable
fun CampaniaCard(campania: Campania, onDonar: () -> Unit) {
    val progreso = (campania.montoRecaudado.toFloat() / campania.metaMonto.toFloat()).coerceIn(0f, 1f)
    val completada = progreso >= 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = campania.imagenUrl,
                contentDescription = campania.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(campania.nombre, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    campania.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (completada) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$${"%,d".format(campania.montoRecaudado)} CLP",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (completada) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Meta: $${"%,d".format(campania.metaMonto)} CLP",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = onDonar,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !completada,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (completada) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (completada) "¡Meta alcanzada!" else "Donar a esta campaña",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}