package com.example.appdortarchile20.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.appdortarchile20.data.model.TipoUrgencia
import com.example.appdortarchile20.data.model.UrgenciaReporte
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

fun createColoredMarkerBitmap(colorHex: Long): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colorHex.toInt() }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, paint)
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, strokePaint)
    return bitmap
}

fun createResueltaBitmap(): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.parseColor("#9E9E9E") }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, paint)
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, strokePaint)
    return bitmap
}

@Composable
fun UrgenciasScreen(viewModel: PetViewModel) {
    val reportes by viewModel.allReportes.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var reporteSeleccionado by remember { mutableStateOf<UrgenciaReporte?>(null) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var chatReporteId by remember { mutableStateOf<Int?>(null) }
    var chatOwnerEmail by remember { mutableStateOf("") }
    var chatTituloAlerta by remember { mutableStateOf("") }

    // Actualizar marcadores cuando cambian los reportes
    LaunchedEffect(reportes) {
        val mapView = mapViewRef ?: return@LaunchedEffect
        mapView.overlays.clear()

        reportes.forEach { reporte ->
            val tipo = TipoUrgencia.entries.find { it.name == reporte.tipo } ?: TipoUrgencia.EXTRAVIADO
            val marker = Marker(mapView).apply {
                position = GeoPoint(reporte.latitud, reporte.longitud)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = reporte.titulo
                snippet = reporte.descripcion

                val bmp = if (reporte.resuelta) createResueltaBitmap()
                else createColoredMarkerBitmap(tipo.colorHex)

                icon = android.graphics.drawable.BitmapDrawable(mapView.context.resources, bmp)

                // Al tocar el pin abre el dialog de detalle
                setOnMarkerClickListener { _, _ ->
                    reporteSeleccionado = reporte
                    true
                }
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    // Dialog de crear alerta
    if (showDialog) {
        CrearAlertaDialog(
            onDismiss = { showDialog = false },
            onConfirm = { reporte -> viewModel.addReporte(reporte) },
            userEmail = currentUser?.email ?: ""
        )
    }

    if (chatReporteId != null) {
        ChatScreen(
            petId = chatReporteId!!,
            petNombre = "🚨 $chatTituloAlerta",
            otroUsuarioEmail = chatOwnerEmail,
            otroUsuarioNombre = chatOwnerEmail,
            viewModel = viewModel,
            onBack = { chatReporteId = null },
            esUrgencia = true
        )
        return
    }

    // Dialog de detalle del reporte al tocar un pin
    if (reporteSeleccionado != null) {
        val reporte = reporteSeleccionado!!
        val tipo = TipoUrgencia.entries.find { it.name == reporte.tipo } ?: TipoUrgencia.EXTRAVIADO
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(reporte.horaReporte))
        val esCreador = currentUser?.email == reporte.reportadoPor
        val esAdmin = currentUser?.isAdmin == true
        val esTercero = !esCreador && currentUser?.email != null

        AlertDialog(
            onDismissRequest = { reporteSeleccionado = null },
            shape = RoundedCornerShape(20.dp),
            icon = {
                Icon(
                    if (reporte.resuelta) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (reporte.resuelta) MaterialTheme.colorScheme.secondary
                    else Color(tipo.colorHex.toInt())
                )
            },
            title = { Text(reporte.titulo, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = if (reporte.resuelta) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (reporte.resuelta) "✓ Resuelta" else tipo.descripcion,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (reporte.resuelta) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    if (reporte.descripcion.isNotEmpty()) {
                        Text(reporte.descripcion, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Reportado el $fecha",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (reporte.reportadoPor.isNotEmpty()) {
                        Text("Por: ${reporte.reportadoPor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.height(4.dp))

                    // Botón chat — solo si es tercero
                    if (esTercero && reporte.reportadoPor.isNotEmpty()) {
                        Button(
                            onClick = {
                                chatReporteId = -reporte.id
                                chatOwnerEmail = reporte.reportadoPor
                                chatTituloAlerta = reporte.titulo
                                reporteSeleccionado = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Contactar al creador")
                        }
                    }

                    // Botón marcar resuelta — solo creador o admin
                    if (!reporte.resuelta && (esCreador || esAdmin)) {
                        Button(
                            onClick = {
                                viewModel.marcarResuelta(reporte.id)
                                reporteSeleccionado = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Marcar como resuelta")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { reporteSeleccionado = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Leyenda de colores
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.93f),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        TipoUrgencia.entries.forEach { tipo ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = androidx.compose.ui.graphics.Color(tipo.colorHex.toInt()),
                                    shape = RoundedCornerShape(50.dp),
                                    modifier = Modifier.size(12.dp)
                                ) {}
                                Text(
                                    tipo.descripcion,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        // Resuelta
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = androidx.compose.ui.graphics.Color(0xFF9E9E9E.toInt()),
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier.size(12.dp)
                            ) {}
                            Text(
                                "Resuelta",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color(0xFFEF5350),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.AddLocation, contentDescription = "Reportar urgencia")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(-33.4489, -70.6693))
                    }.also { mapViewRef = it }
                },
                update = { mapView -> mapViewRef = mapView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}