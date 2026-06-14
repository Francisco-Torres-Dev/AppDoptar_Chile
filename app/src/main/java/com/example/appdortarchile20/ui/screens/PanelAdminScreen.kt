package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appdortarchile20.data.model.Pet
import com.example.appdortarchile20.data.model.UrgenciaReporte
import com.example.appdortarchile20.ui.viewmodel.PetViewModel

@Composable
fun PanelAdminScreen(viewModel: PetViewModel) {
    val allPets by viewModel.allPets.collectAsState()
    val petsEliminadas by viewModel.petsEliminadas.collectAsState()
    val allReportes by viewModel.allReportes.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var petAEliminar by remember { mutableStateOf<Pet?>(null) }
    var reporteAEliminar by remember { mutableStateOf<UrgenciaReporte?>(null) }

    // Dialog eliminar mascota
    if (petAEliminar != null) {
        AlertDialog(
            onDismissRequest = { petAEliminar = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null,
                tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Eliminar publicación?", fontWeight = FontWeight.Bold) },
            text = { Text("Se eliminará la publicación de ${petAEliminar!!.name} permanentemente.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deletePet(petAEliminar!!); petAEliminar = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { petAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    // Dialog eliminar reporte
    if (reporteAEliminar != null) {
        AlertDialog(
            onDismissRequest = { reporteAEliminar = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null,
                tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Eliminar reporte?", fontWeight = FontWeight.Bold) },
            text = { Text("Se eliminará el reporte de urgencia permanentemente.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteReporte(reporteAEliminar!!); reporteAEliminar = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { reporteAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    FondoHuellas {
        Column(modifier = Modifier.fillMaxSize()) {

            // Banner admin
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(32.dp))
                    Column {
                        Text("Panel de Administración",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Gestión total de la plataforma",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            // Stats rápidas
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("Activas", allPets.size.toString(),
                    Icons.Default.Pets, Modifier.weight(1f))
                StatCard("Eliminadas", petsEliminadas.size.toString(),
                    Icons.Default.Delete, Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.error)
                StatCard("Urgencias", allReportes.size.toString(),
                    Icons.Default.Warning, Modifier.weight(1f),
                    color = Color(0xFFF57C00))
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("Mascotas") },
                    icon = { Icon(Icons.Default.Pets, contentDescription = null) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("Historial") },
                    icon = { Icon(Icons.Default.History, contentDescription = null) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                    text = { Text("Urgencias") },
                    icon = { Icon(Icons.Default.Warning, contentDescription = null) })
            }

            when (selectedTab) {
                0 -> {
                    if (allPets.isEmpty()) {
                        EmptyAdmin("No hay mascotas publicadas")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(allPets) { pet ->
                                AdminPetCard(pet = pet, onEliminar = { petAEliminar = pet })
                            }
                        }
                    }
                }
                1 -> {
                    if (petsEliminadas.isEmpty()) {
                        EmptyAdmin("No hay mascotas eliminadas")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(petsEliminadas) { pet ->
                                AdminPetEliminadaCard(
                                    pet = pet,
                                    onRestaurar = { viewModel.restaurarPet(pet) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    if (allReportes.isEmpty()) {
                        EmptyAdmin("No hay reportes de urgencia")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(allReportes) { reporte ->
                                AdminReporteCard(
                                    reporte = reporte,
                                    onMarcarResuelta = { viewModel.marcarResuelta(reporte.id) },
                                    onEliminar = { reporteAEliminar = reporte }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    titulo: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Column {
                Text(valor, style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black, color = color)
                Text(titulo, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AdminPetCard(pet: Pet, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(model = pet.imageUrl, contentDescription = pet.name,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop)
            Column(modifier = Modifier.weight(1f)) {
                Text(pet.name, fontWeight = FontWeight.Bold)
                Text("${pet.type} · ${pet.age}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${pet.city}, ${pet.region}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Dueño: ${pet.ownerName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AdminPetEliminadaCard(pet: Pet, onRestaurar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(model = pet.imageUrl, contentDescription = pet.name,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(pet.name, fontWeight = FontWeight.Bold)
                    Surface(color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(20.dp)) {
                        Text("Eliminada", style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Text("${pet.type} · ${pet.age}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Dueño: ${pet.ownerName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onRestaurar) {
                Icon(Icons.Default.RestoreFromTrash, contentDescription = null,
                    modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Restaurar")
            }
        }
    }
}

@Composable
fun AdminReporteCard(
    reporte: UrgenciaReporte,
    onMarcarResuelta: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reporte.resuelta)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = if (reporte.resuelta) Color.Gray
                    else Color(0xFFEF5350),
                        shape = RoundedCornerShape(20.dp)) {
                        Text(if (reporte.resuelta) "Resuelta" else reporte.tipo,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                    Text(reporte.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = onEliminar, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
            Text("Lat: ${reporte.latitud}, Lng: ${reporte.longitud}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!reporte.resuelta) {
                TextButton(onClick = onMarcarResuelta,
                    modifier = Modifier.align(Alignment.End)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Marcar como resuelta")
                }
            }
        }
    }
}

@Composable
fun EmptyAdmin(mensaje: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(mensaje, style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center)
    }
}