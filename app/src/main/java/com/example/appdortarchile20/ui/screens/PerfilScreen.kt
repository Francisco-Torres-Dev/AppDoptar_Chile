package com.example.appdortarchile20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appdortarchile20.data.ChileData
import com.example.appdortarchile20.ui.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(viewModel: PetViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()

    var editando by remember { mutableStateOf(false) }
    var guardado by remember { mutableStateOf(false) }

    // Campos editables
    var nombre by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var telefono by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }
    var selectedRegion by remember(currentUser) { mutableStateOf(currentUser?.region ?: "") }
    var expandedRegion by remember { mutableStateOf(false) }

    FondoHuellas {
        if (currentUser == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay usuario logueado", style = MaterialTheme.typography.bodyLarge)
            }
            return@FondoHuellas
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Avatar con inicial del nombre
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = currentUser!!.name.firstOrNull()?.uppercase() ?: "?",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        currentUser!!.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        currentUser!!.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Datos del perfil
            if (!editando) {

                // Modo vista
                listOf(
                    Triple(Icons.Default.Person, "Nombre", currentUser!!.name),
                    Triple(Icons.Default.Phone, "Teléfono", currentUser!!.phone.ifEmpty { "No registrado" }),
                    Triple(Icons.Default.LocationOn, "Región", currentUser!!.region.ifEmpty { "No registrada" }),
                    Triple(Icons.Default.Cake, "Edad", if (currentUser!!.age > 0) "${currentUser!!.age} años" else "No registrada"),
                    Triple(Icons.Default.Email, "Correo", currentUser!!.email),
                ).forEach { (icon, label, valor) ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(icon, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp))
                            Column {
                                Text(label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(valor,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                if (guardado) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp))
                            Text("Perfil actualizado correctamente",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }

                Button(
                    onClick = { editando = true; guardado = false },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Editar perfil", fontWeight = FontWeight.Bold)
                }

            } else {

                // Modo edición
                Text("Editar perfil",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it.filter { c -> c.isDigit() || c == '+' } },
                    label = { Text("Teléfono (+569...)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expandedRegion,
                    onExpandedChange = { expandedRegion = !expandedRegion }
                ) {
                    OutlinedTextField(
                        value = selectedRegion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Región") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRegion,
                        onDismissRequest = { expandedRegion = false }
                    ) {
                        ChileData.regionesChile.forEach { region ->
                            DropdownMenuItem(
                                text = { Text(region) },
                                onClick = { selectedRegion = region; expandedRegion = false }
                            )
                        }
                    }
                }

                // Datos no editables
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Datos no editables",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Correo: ${currentUser!!.email}",
                            style = MaterialTheme.typography.bodySmall)
                        Text("Edad: ${if (currentUser!!.age > 0) "${currentUser!!.age} años" else "No registrada"}",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            // Restaurar valores originales
                            nombre = currentUser!!.name
                            telefono = currentUser!!.phone
                            selectedRegion = currentUser!!.region
                            editando = false
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val usuarioActualizado = currentUser!!.copy(
                                name = nombre.trim(),
                                phone = telefono.trim(),
                                region = selectedRegion
                            )
                            viewModel.updateUser(usuarioActualizado)
                            editando = false
                            guardado = true
                        },
                        modifier = Modifier.weight(2f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = nombre.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar cambios", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}