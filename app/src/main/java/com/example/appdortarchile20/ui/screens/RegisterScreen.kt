package com.example.appdortarchile20.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appdortarchile20.R
import com.example.appdortarchile20.data.model.User
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import com.example.appdortarchile20.ui.viewmodel.LoginState
import com.example.appdortarchile20.data.ChileData

private fun validarEmail(email: String): String? {
    if (email.isEmpty()) return "El correo es obligatorio"
    val regex = Regex("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")
    if (!regex.matches(email)) return "Ingresa un correo válido (ej: usuario@mail.com)"
    return null
}

private fun validarPassword(password: String): String? {
    if (password.isEmpty()) return "La contraseña es obligatoria"
    if (password.length < 6) return "Mínimo 6 caracteres"
    return null
}

private fun validarNombre(nombre: String): String? {
    if (nombre.isEmpty()) return "El nombre es obligatorio"
    if (nombre.trim().length < 3) return "Mínimo 3 caracteres"
    if (!nombre.all { it.isLetter() || it.isWhitespace() }) return "Solo se permiten letras"
    return null
}

private fun validarTelefono(telefono: String): String? {
    if (telefono.length != 8) return "Ingresa los 8 dígitos de tu celular"
    return null
}

private fun calcularEdad(fechaNacimiento: Long): Int {
    val hoy = java.util.Calendar.getInstance()
    val nacimiento = java.util.Calendar.getInstance().apply { timeInMillis = fechaNacimiento }
    var edad = hoy.get(java.util.Calendar.YEAR) - nacimiento.get(java.util.Calendar.YEAR)
    if (hoy.get(java.util.Calendar.DAY_OF_YEAR) < nacimiento.get(java.util.Calendar.DAY_OF_YEAR)) edad--
    return edad
}

private fun formatearFecha(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: PetViewModel, onRegistered: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Colores para campos sobre fondo azul — definidos dentro del composable
    val campoColores = OutlinedTextFieldDefaults.colors(
        unfocusedTextColor = Color.White,
        focusedTextColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedBorderColor = Color.White,
        cursorColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        focusedLabelColor = Color.White,
        unfocusedLeadingIconColor = Color.White.copy(alpha = 0.7f),
        focusedLeadingIconColor = Color.White,
        unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f),
        focusedTrailingIconColor = Color.White,
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
        focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
        errorBorderColor = Color(0xFFFFCDD2),
        errorLabelColor = Color(0xFFFFCDD2),
        errorTextColor = Color.White,
        errorCursorColor = Color(0xFFFFCDD2),
        errorLeadingIconColor = Color(0xFFFFCDD2)
    )
    var selectedRegion by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var intentado by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) onRegistered()
    }

    val errorNombre   = if (intentado) validarNombre(name) else null
    val errorTelefono = if (intentado) validarTelefono(phone) else null
    val errorFecha = if (intentado) when {
        fechaNacimiento == null -> "Selecciona tu fecha de nacimiento"
        calcularEdad(fechaNacimiento!!) < 18 -> "Debes tener al menos 18 años para registrarte"
        else -> null
    } else null
    val errorEmail    = if (intentado) validarEmail(email) else null
    val errorPassword = if (intentado) validarPassword(password) else null
    val errorRegion   = if (intentado && selectedRegion.isEmpty()) "Selecciona una región" else null

    val formularioValido = validarNombre(name) == null &&
            validarTelefono(phone) == null &&
            fechaNacimiento != null &&
            calcularEdad(fechaNacimiento!!) >= 18 &&
            validarEmail(email) == null &&
            validarPassword(password) == null &&
            selectedRegion.isNotEmpty()

    val azulPrincipal = Color(0xFF0038A5)
    val azulClaro     = Color(0xFF1A4FBF)
    val crema         = Color(0xFFD0DEFF)

    // Animación de entrada igual que LoginScreen
    var visible by remember { mutableStateOf(false) }
    val screenAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "ScreenFade"
    )
    val screenOffset by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (visible) 0f else 80f,
        animationSpec = tween(durationMillis = 400),
        label = "ScreenSlide"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = screenAlpha, translationX = screenOffset)
    ) {
        // Fondo gradiente azul
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(colors = listOf(azulPrincipal, azulClaro)))
        )
        // Huellas blancas encima
        FondoHuellas(alpha = 0.12f, color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // Logo pequeño
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row {
                            Text("App",    fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Doptar", fontSize = 20.sp, fontWeight = FontWeight.Black, color = crema)
                            Spacer(Modifier.width(4.dp))
                            Text("Chile",  fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF81C784))
                        }
                        Text("Crea tu cuenta",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                }

                // Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.filter { c -> c.isLetter() || c.isWhitespace() } },
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    isError = errorNombre != null,
                    supportingText = {
                        if (errorNombre != null) Text(errorNombre, color = Color(0xFFFFCDD2))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = campoColores,
                    singleLine = true
                )

                // Teléfono
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.filter { c -> c.isDigit() }.take(8) },
                    label = { Text("Teléfono celular") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    prefix = { Text("+569 ", color = Color.White) },
                    isError = errorTelefono != null,
                    supportingText = {
                        if (errorTelefono != null) Text(errorTelefono, color = Color(0xFFFFCDD2))
                        else Text("${phone.length}/8 dígitos", color = Color.White.copy(alpha = 0.6f))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = campoColores,
                    singleLine = true
                )

                // Región
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedRegion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona tu región") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        isError = errorRegion != null,
                        supportingText = {
                            if (errorRegion != null) Text(errorRegion, color = Color(0xFFFFCDD2))
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = campoColores
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        ChileData.regionesChile.forEach { region ->
                            DropdownMenuItem(
                                text = { Text(region) },
                                onClick = { selectedRegion = region; expanded = false }
                            )
                        }
                    }
                }

                // Fecha de nacimiento
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = fechaNacimiento,
                    yearRange = 1920..java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - 18
                )
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                fechaNacimiento = datePickerState.selectedDateMillis
                                showDatePicker = false
                            }) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                OutlinedTextField(
                    value = if (fechaNacimiento != null) {
                        val edad = calcularEdad(fechaNacimiento!!)
                        "${formatearFecha(fechaNacimiento!!)} ($edad años)"
                    } else "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de nacimiento") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.EditCalendar, contentDescription = "Seleccionar fecha",
                                tint = Color.White)
                        }
                    },
                    isError = errorFecha != null,
                    supportingText = {
                        if (errorFecha != null) Text(errorFecha, color = Color(0xFFFFCDD2))
                        else if (fechaNacimiento == null) Text("Toca el ícono para seleccionar", color = Color.White.copy(alpha = 0.6f))
                    },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = campoColores
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    isError = errorEmail != null,
                    supportingText = {
                        if (errorEmail != null) Text(errorEmail, color = Color(0xFFFFCDD2))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = campoColores,
                    singleLine = true
                )

                // Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = errorPassword != null,
                    supportingText = {
                        if (errorPassword != null) Text(errorPassword, color = Color(0xFFFFCDD2))
                        else Text("Mínimo 6 caracteres", color = Color.White.copy(alpha = 0.6f))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = campoColores,
                    singleLine = true
                )

                if (loginState is LoginState.Error) {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            (loginState as LoginState.Error).message,
                            color = Color(0xFFFFCDD2),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = {
                        intentado = true
                        if (formularioValido) {
                            viewModel.register(
                                User(
                                    id = 0,
                                    name = name.trim(),
                                    phone = "+569${phone.trim()}",
                                    region = selectedRegion,
                                    age = if (fechaNacimiento != null) calcularEdad(fechaNacimiento!!) else 0,
                                    email = email.trim(),
                                    password = password
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Crear mi cuenta", color = azulPrincipal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}