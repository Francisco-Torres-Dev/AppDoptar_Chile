package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appdortarchile20.data.model.Pet
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    petId: Int,
    petNombre: String,
    otroUsuarioEmail: String,
    otroUsuarioNombre: String,
    viewModel: PetViewModel,
    onBack: () -> Unit,
    pet: Pet? = null,
    esUrgencia: Boolean = false
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var nombreOtroUsuario by remember { mutableStateOf("") }
    var tituloChat by remember { mutableStateOf(petNombre) }

    LaunchedEffect(otroUsuarioEmail, petId) {
        nombreOtroUsuario = viewModel.getNombreUsuario(otroUsuarioEmail)
        // Si es urgencia (petId negativo), cargar el título del reporte
        if (petId < 0) {
            val titulo = viewModel.getTituloReporte(petId)
            if (titulo.isNotEmpty()) tituloChat = "🚨 $titulo"
        }
    }

    // Fix parpadeo: remember estabiliza el StateFlow
    val mensajes by remember(petId, currentUser?.email, otroUsuarioEmail) {
        viewModel.getMensajesChat(petId, currentUser?.email ?: "", otroUsuarioEmail)
    }.collectAsState()

    var textoMensaje by remember { mutableStateOf("") }
    var showEvaluacionDialog by remember { mutableStateOf(false) }
    var showPetInfo by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { viewModel.marcarComoLeidos(petId) }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) listState.animateScrollToItem(mensajes.size - 1)
    }

    if (showEvaluacionDialog) {
        EvaluacionDialog(
            nombreEvaluado = nombreOtroUsuario,
            onDismiss = { showEvaluacionDialog = false },
            onConfirm = { estrellas, comentario ->
                viewModel.enviarEvaluacion(petId, otroUsuarioEmail, estrellas, comentario)
                showEvaluacionDialog = false
            }
        )
    }

    // BottomSheet con info de la mascota
    if (showPetInfo && pet != null) {
        ModalBottomSheet(
            onDismissRequest = { showPetInfo = false },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Mascota en adopción",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)

                AsyncImage(
                    model = pet.imageUrl,
                    contentDescription = pet.name,
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(20.dp)) {
                        Text(pet.name, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(20.dp)) {
                        Text(pet.type, style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp)) {
                        Text("${pet.age} años", style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Text("${pet.city}, ${pet.region}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (pet.hasVaccines || pet.isSterilized) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (pet.hasVaccines) {
                            Surface(color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(20.dp)) {
                                Text("✓ Vacunado", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                        if (pet.isSterilized) {
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp)) {
                                Text("✓ Esterilizado", style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                    }
                }

                if (pet.description.isNotEmpty()) {
                    Text(pet.description, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Avatar con inicial
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()) {
                                Text(
                                    nombreOtroUsuario.firstOrNull()?.uppercase() ?: "?",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Column {
                            Text(tituloChat,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White)
                            Text("Con $nombreOtroUsuario",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (pet != null) {
                        IconButton(onClick = { showPetInfo = true }) {
                            Icon(Icons.Default.Pets, contentDescription = "Info mascota", tint = Color.White)
                        }
                    }
                    IconButton(onClick = { showEvaluacionDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = "Evaluar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = textoMensaje,
                        onValueChange = { textoMensaje = it },
                        placeholder = { Text("Escribe un mensaje...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    FloatingActionButton(
                        onClick = {
                            if (textoMensaje.isNotBlank()) {
                                viewModel.enviarMensaje(petId, otroUsuarioEmail, textoMensaje.trim())
                                textoMensaje = ""
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar",
                            tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { padding ->
        if (mensajes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Chat, contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    Text("¡Inicia la conversación!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        if (esUrgencia)
                            "Coordina el rescate o ayuda con $nombreOtroUsuario."
                        else
                            "Pregunta sobre $tituloChat y coordina la adopción.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(mensajes, key = { it.id }) { mensaje ->
                    BurbujaMensaje(
                        mensaje = mensaje,
                        esMio = mensaje.remitenteEmail == currentUser?.email
                    )
                }
            }
        }
    }
}

@Composable
fun BurbujaMensaje(mensaje: com.example.appdortarchile20.data.model.Mensaje, esMio: Boolean) {
    val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(mensaje.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start
    ) {
        if (!esMio) {
            Surface(shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(32.dp).align(Alignment.Bottom)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(mensaje.remitenteEmail.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.width(6.dp))
        }

        Column(horizontalAlignment = if (esMio) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 260.dp)) {
            Surface(
                color = if (esMio) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (esMio) 16.dp else 4.dp,
                    bottomEnd = if (esMio) 4.dp else 16.dp
                ),
                shadowElevation = if (esMio) 0.dp else 1.dp
            ) {
                Text(text = mensaje.texto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (esMio) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
            }
            Text(text = hora, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
        }

        if (esMio) Spacer(Modifier.width(6.dp))
    }
}

@Composable
fun EvaluacionDialog(
    nombreEvaluado: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var estrellas by remember { mutableStateOf(0) }
    var comentario by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Icon(Icons.Default.Star, contentDescription = null,
                tint = Color(0xFFFFC107), modifier = Modifier.size(32.dp))
        },
        title = {
            Text("Evaluar a $nombreEvaluado", fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()) {
                Text("¿Cómo fue tu experiencia en el proceso de adopción?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center)

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..5).forEach { i ->
                        IconButton(onClick = { estrellas = i },
                            modifier = Modifier.size(40.dp)) {
                            Icon(
                                if (i <= estrellas) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "$i estrellas",
                                tint = if (i <= estrellas) Color(0xFFFFC107)
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                if (estrellas > 0) {
                    Text(
                        when (estrellas) {
                            1 -> "😞 Mala experiencia"
                            2 -> "😕 Podría mejorar"
                            3 -> "😊 Experiencia normal"
                            4 -> "😄 Muy buena experiencia"
                            else -> "🤩 ¡Excelente experiencia!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = when (estrellas) {
                            1, 2 -> MaterialTheme.colorScheme.error
                            3 -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                }

                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(estrellas, comentario) },
                enabled = estrellas > 0, shape = RoundedCornerShape(12.dp)) {
                Text("Enviar evaluación")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}