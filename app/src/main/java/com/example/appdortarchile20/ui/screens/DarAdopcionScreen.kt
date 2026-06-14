package com.example.appdortarchile20.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.appdortarchile20.data.ChileData
import com.example.appdortarchile20.data.model.Pet
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarAdopcionScreen(viewModel: PetViewModel, onSaved: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Perro") }
    var age by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var hasVaccines by remember { mutableStateOf(false) }
    var isSterilized by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showFotoDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // URI temporal para la foto de cámara
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) imageUri = uri }
    )

    // Lanzador de cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) imageUri = cameraUri }
    )

    // Permiso de cámara
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                val file = File.createTempFile("foto_mascota_", ".jpg", context.cacheDir)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                cameraUri = uri
                cameraLauncher.launch(uri)
            }
        }
    )

    var intentado by remember { mutableStateOf(false) }

    val errorNombre = if (intentado) when {
        name.isEmpty() -> "El nombre es obligatorio"
        name.trim().length < 2 -> "Mínimo 2 caracteres"
        else -> null
    } else null
    val errorEdad = if (intentado) when {
        age.isEmpty() -> "La edad es obligatoria"
        else -> null
    } else null
    val errorCiudad = if (intentado) when {
        city.isEmpty() -> "La ciudad es obligatoria"
        city.trim().length < 2 -> "Ingresa una ciudad válida"
        else -> null
    } else null
    val errorRegion = if (intentado && selectedRegion.isEmpty()) "Selecciona una región" else null
    val errorFoto = if (intentado && imageUri == null) "La foto es obligatoria" else null
    val errorDescripcion = if (intentado && description.isEmpty()) "Agrega una descripción" else null

    val formularioValido = name.isNotEmpty() && name.trim().length >= 2 &&
            age.isNotEmpty() && city.isNotEmpty() && city.trim().length >= 2 &&
            selectedRegion.isNotEmpty() && imageUri != null && description.isNotEmpty()

    // Dialog para elegir entre cámara o galería
    if (showFotoDialog) {
        AlertDialog(
            onDismissRequest = { showFotoDialog = false },
            icon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Agregar foto", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Opción cámara
                    OutlinedButton(
                        onClick = {
                            showFotoDialog = false
                            permisoCamaraLauncher.launch(android.Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Tomar foto con cámara")
                    }
                    // Opción galería
                    OutlinedButton(
                        onClick = {
                            showFotoDialog = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Photo, contentDescription = null,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Elegir desde galería")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showFotoDialog = false }) { Text("Cancelar") }
            }
        )
    }

    val currentUser by viewModel.currentUser.collectAsState()
    var expandedRegion by remember { mutableStateOf(false) }

    FondoHuellas {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Publicar Mascota",
                style = MaterialTheme.typography.headlineMedium
            )

            // --- FOTO ---
            Card(
                modifier = Modifier.fillMaxWidth().height(210.dp),
                onClick = {
                    showFotoDialog = true
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (imageUri == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(52.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Toca para subir foto",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Foto seleccionada",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // --- DATOS BÁSICOS ---
            OutlinedTextField(
                value = name,
                onValueChange = { input ->
                    // Solo letras (incluyendo tildes), espacios
                    name = input.filter { it.isLetter() || it.isWhitespace() }
                },
                label = { Text("Nombre de la mascota") },
                leadingIcon = {
                    Icon(Icons.Default.Pets, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                isError = errorNombre != null,
                supportingText = { if (errorNombre != null) Text(errorNombre, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // Tipo de animal
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Tipo de animal",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("Perro", "Gato", "Otro").forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                RadioButton(
                                    selected = type == option,
                                    onClick = { type = option },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(option, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = age,
                onValueChange = { input ->
                    // Solo números, máximo 2 dígitos
                    age = input.filter { it.isDigit() }.take(2)
                },
                label = { Text("Edad en años (ej: 3)") },
                isError = errorEdad != null,
                supportingText = { if (errorEdad != null) Text(errorEdad, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // --- UBICACIÓN ---
            ExposedDropdownMenuBox(
                expanded = expandedRegion,
                onExpandedChange = { expandedRegion = !expandedRegion }
            ) {
                OutlinedTextField(
                    value = selectedRegion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Región") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                    isError = errorRegion != null,
                    supportingText = { if (errorRegion != null) Text(errorRegion, color = MaterialTheme.colorScheme.error) },
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

            OutlinedTextField(
                value = city,
                onValueChange = { input ->
                    city = input.filter { it.isLetter() || it.isWhitespace() }
                },
                label = { Text("Ciudad") },
                isError = errorCiudad != null,
                supportingText = { if (errorCiudad != null) Text(errorCiudad, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // --- SALUD ---
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Salud",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = hasVaccines,
                            onCheckedChange = { hasVaccines = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Vacunas al día", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.width(16.dp))
                        Checkbox(
                            checked = isSterilized,
                            onCheckedChange = { isSterilized = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Esterilizado", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(14.dp),
                isError = errorDescripcion != null,
                supportingText = { if (errorDescripcion != null) Text(errorDescripcion, color = MaterialTheme.colorScheme.error) },
                maxLines = 5
            )

            // Mensaje error foto
            if (errorFoto != null) {
                Text(errorFoto, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            // --- BOTÓN ---
            Button(
                onClick = {
                    intentado = true
                    if (formularioValido) {
                        val newPet = Pet(
                            id = 0,
                            name = name,
                            type = type,
                            age = age,
                            region = selectedRegion,
                            city = city,
                            imageUrl = imageUri?.toString() ?: "https://placedog.net/500",
                            hasVaccines = hasVaccines,
                            isSterilized = isSterilized,
                            description = description,
                            ownerName = currentUser?.name ?: "Anónimo",
                            ownerPhone = currentUser?.phone ?: "",
                            ownerEmail = currentUser?.email ?: ""
                        )
                        viewModel.addPet(newPet)
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Publicar Mascota", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}