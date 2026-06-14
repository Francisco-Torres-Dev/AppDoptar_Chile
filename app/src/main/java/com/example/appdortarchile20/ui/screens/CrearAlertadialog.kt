package com.example.appdortarchile20.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.appdortarchile20.data.model.TipoUrgencia
import com.example.appdortarchile20.data.model.UrgenciaReporte
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@SuppressLint("MissingPermission")
@Composable
fun CrearAlertaDialog(
    onDismiss: () -> Unit,
    onConfirm: (UrgenciaReporte) -> Unit,
    userEmail: String = ""
) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoUrgencia.EXTRAVIADO) }

    // Estado de ubicación
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }
    var obteniendoUbicacion by remember { mutableStateOf(false) }
    var errorUbicacion by remember { mutableStateOf(false) }

    val tienePermiso = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Función para obtener ubicación
    fun obtenerUbicacion() {
        obteniendoUbicacion = true
        errorUbicacion = false
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                obteniendoUbicacion = false
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                } else {
                    // Si getCurrentLocation falla, intentar con lastLocation
                    fusedLocationClient.lastLocation.addOnSuccessListener { last ->
                        if (last != null) {
                            latitud = last.latitude
                            longitud = last.longitude
                        } else {
                            // Fallback a Santiago si no hay ubicación disponible
                            latitud = -33.4489
                            longitud = -70.6693
                        }
                        obteniendoUbicacion = false
                    }
                }
            }
            .addOnFailureListener {
                obteniendoUbicacion = false
                errorUbicacion = true
                // Fallback a Santiago
                latitud = -33.4489
                longitud = -70.6693
            }
    }

    // Lanzador de permiso
    val permisosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) obtenerUbicacion()
        else { latitud = -33.4489; longitud = -70.6693 }
    }

    // Obtener ubicación al abrir el dialog
    LaunchedEffect(Unit) {
        if (tienePermiso) obtenerUbicacion()
        else permisosLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Reportar Urgencia", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Estado de ubicación
                Surface(
                    color = when {
                        obteniendoUbicacion -> MaterialTheme.colorScheme.surfaceVariant
                        errorUbicacion -> MaterialTheme.colorScheme.errorContainer
                        latitud != null -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (obteniendoUbicacion) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Text("Obteniendo ubicación...", style = MaterialTheme.typography.bodySmall)
                        } else if (latitud != null) {
                            Icon(Icons.Default.LocationOn, contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Text(
                                "Ubicación obtenida (%.4f, %.4f)".format(latitud, longitud),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        } else {
                            Icon(Icons.Default.LocationOff, contentDescription = null,
                                tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Text("Sin ubicación — usando Santiago",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }

                // Campo Título
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título del reporte") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Campo Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )

                // Selector de Tipo
                Text("Tipo de urgencia:", style = MaterialTheme.typography.titleSmall)
                Column {
                    TipoUrgencia.entries.forEach { tipo ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = tipoSeleccionado == tipo,
                                onClick = { tipoSeleccionado = tipo },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(tipo.descripcion, fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reporte = UrgenciaReporte(
                        titulo = titulo,
                        descripcion = descripcion,
                        tipo = tipoSeleccionado.name,
                        latitud = latitud ?: -33.4489,
                        longitud = longitud ?: -70.6693,
                        reportadoPor = userEmail
                    )
                    onConfirm(reporte)
                    onDismiss()
                },
                enabled = titulo.isNotEmpty() && !obteniendoUbicacion,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reportar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}