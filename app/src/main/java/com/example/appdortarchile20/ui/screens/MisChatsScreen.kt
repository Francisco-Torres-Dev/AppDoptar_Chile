package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MisChatsScreen(
    viewModel: PetViewModel,
    onAbrirChat: (petId: Int, otroEmail: String, otroNombre: String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allPets by viewModel.allPets.collectAsState()

    val todosMensajes by remember(currentUser?.email) {
        viewModel.getMensajesUsuario(currentUser?.email ?: "")
    }.collectAsState()

    val conversaciones = remember(todosMensajes, currentUser?.email) {
        todosMensajes
            .groupBy { msg ->
                val otro = if (msg.remitenteEmail == currentUser?.email)
                    msg.destinatarioEmail else msg.remitenteEmail
                "${msg.petId}_$otro"
            }
            .map { (_, msgs) -> msgs.maxByOrNull { it.timestamp }!! }
            .sortedByDescending { it.timestamp }
    }

    FondoHuellas {
        if (conversaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    Text("Sin conversaciones aún",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Inicia un chat desde la tarjeta de una mascota.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(conversaciones) { ultimoMsg ->
                    val esMio = ultimoMsg.remitenteEmail == currentUser?.email
                    val otroEmail = if (esMio) ultimoMsg.destinatarioEmail else ultimoMsg.remitenteEmail
                    val esUrgencia = ultimoMsg.petId < 0
                    val pet = if (esUrgencia) null else allPets.find { it.id == ultimoMsg.petId }
                    val titulo = if (esUrgencia) "🚨 Urgencia #${-ultimoMsg.petId}"
                    else pet?.name ?: "Conversación"
                    val hora = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(ultimoMsg.timestamp))

                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            onAbrirChat(
                                ultimoMsg.petId,
                                otroEmail,
                                otroEmail
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(shape = CircleShape,
                                color = if (esUrgencia) MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(48.dp)) {
                                Box(contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        if (esUrgencia) "🚨" else otroEmail.firstOrNull()?.uppercase() ?: "?",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (esUrgencia) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(titulo,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold)
                                    Text(hora, style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    if (esMio) "Tú: ${ultimoMsg.texto}" else ultimoMsg.texto,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                                pet?.let {
                                    Surface(color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.padding(top = 4.dp)) {
                                        Text("${it.type} · ${it.city}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}