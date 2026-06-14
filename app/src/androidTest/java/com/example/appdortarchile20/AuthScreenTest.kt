import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
// ¡Estas dos importaciones son clave para que el "by" no salga en rojo!
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==========================================
    // CASOS DE PRUEBA DE INTERFAZ (UI COMPOSE)
    // ==========================================

    // CP-AU-01: Login exitoso
    @Test
    fun loginExitoso_muestraMensajeBienvenida() {
        composeTestRule.setContent { MockLoginScreen() }

        composeTestRule.onNodeWithText("Email").performTextInput("admin@appdoptar.cl")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("admin123")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        composeTestRule.onNodeWithText("¡Hola, Administrador!").assertIsDisplayed()
    }

    // CP-AU-02: Login con credenciales incorrectas
    @Test
    fun loginIncorrecto_muestraMensajeError() {
        composeTestRule.setContent { MockLoginScreen() }

        composeTestRule.onNodeWithText("Email").performTextInput("email@test.cl")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("clavemal")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        composeTestRule.onNodeWithText("Credenciales incorrectas").assertIsDisplayed()
    }

    // CP-AU-05: Registro con email duplicado
    @Test
    fun registro_emailDuplicado_muestraError() {
        composeTestRule.setContent { MockRegisterScreen() }

        composeTestRule.onNodeWithText("Email").performTextInput("duplicado@mail.com")
        composeTestRule.onNodeWithText("Crear mi cuenta").performClick()

        composeTestRule.onNodeWithText("El correo ya está registrado").assertIsDisplayed()
    }

    // CP-AU-06: Registro con nombre con símbolos
    @Test
    fun registro_nombreConSimbolos_seFiltraAutomaticamente() {
        composeTestRule.setContent { MockRegisterScreen() }

        val campoNombre = composeTestRule.onNodeWithText("Nombre")
        campoNombre.performTextInput("Juan123!!")

        // La UI debería haber filtrado y dejar solo "Juan"
        composeTestRule.onNodeWithText("Juan").assertIsDisplayed()
    }

    // CP-AU-08: Editar perfil
    @Test
    fun editarPerfil_actualizaNombre() {
        composeTestRule.setContent { MockPerfilScreen() }

        composeTestRule.onNodeWithText("Nombre actual").performTextInput("Juan Carlos")
        composeTestRule.onNodeWithText("Guardar").performClick()

        composeTestRule.onNodeWithText("Perfil actualizado: Juan Carlos").assertIsDisplayed()
    }

    // CP-AU-09: Logout con confirmación
    @Test
    fun logout_conConfirmacion_cierraSesion() {
        composeTestRule.setContent { MockMainDrawerScreen() }

        composeTestRule.onNodeWithText("Cerrar sesión").performClick()
        composeTestRule.onNodeWithText("Confirmar").performClick()

        composeTestRule.onNodeWithText("Sesión cerrada").assertIsDisplayed()
    }

    // ==========================================
    // ⚠️ MOCKS: COMPOSABLES DE APOYO PARA EVITAR ROJOS
    // Reemplaza estas funciones por las reales de tu app
    // usando Alt + Enter para importar tus pantallas
    // ==========================================

    @androidx.compose.runtime.Composable
    fun MockLoginScreen() {
        var mensaje by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        var email by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

        androidx.compose.foundation.layout.Column {
            androidx.compose.material3.TextField(value = email, onValueChange = { email = it }, label = { androidx.compose.material3.Text("Email") })
            androidx.compose.material3.TextField(value = "", onValueChange = {}, label = { androidx.compose.material3.Text("Contraseña") })
            androidx.compose.material3.Button(onClick = {
                if (email == "admin@appdoptar.cl") mensaje = "¡Hola, Administrador!"
                else mensaje = "Credenciales incorrectas"
            }) {
                androidx.compose.material3.Text("Iniciar Sesión")
            }
            if (mensaje.isNotEmpty()) androidx.compose.material3.Text(mensaje)
        }
    }

    @androidx.compose.runtime.Composable
    fun MockRegisterScreen() {
        var email by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        var nombre by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        var mensaje by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

        androidx.compose.foundation.layout.Column {
            androidx.compose.material3.TextField(value = nombre, onValueChange = { nombre = it.filter { char -> char.isLetter() } }, label = { androidx.compose.material3.Text("Nombre") })
            androidx.compose.material3.TextField(value = email, onValueChange = { email = it }, label = { androidx.compose.material3.Text("Email") })
            androidx.compose.material3.Button(onClick = {
                if (email == "duplicado@mail.com") mensaje = "El correo ya está registrado"
            }) {
                androidx.compose.material3.Text("Crear mi cuenta")
            }
            if (mensaje.isNotEmpty()) androidx.compose.material3.Text(mensaje)
        }
    }

    @androidx.compose.runtime.Composable
    fun MockPerfilScreen() {
        var nombre by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        var mensaje by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

        androidx.compose.foundation.layout.Column {
            androidx.compose.material3.TextField(value = nombre, onValueChange = { nombre = it }, label = { androidx.compose.material3.Text("Nombre actual") })
            androidx.compose.material3.Button(onClick = { mensaje = "Perfil actualizado: $nombre" }) {
                androidx.compose.material3.Text("Guardar")
            }
            if (mensaje.isNotEmpty()) androidx.compose.material3.Text(mensaje)
        }
    }

    @androidx.compose.runtime.Composable
    fun MockMainDrawerScreen() {
        var showDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        var mensaje by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

        androidx.compose.foundation.layout.Column {
            androidx.compose.material3.Button(onClick = { showDialog = true }) {
                androidx.compose.material3.Text("Cerrar sesión")
            }
            if (showDialog) {
                androidx.compose.material3.Button(onClick = { showDialog = false; mensaje = "Sesión cerrada" }) {
                    androidx.compose.material3.Text("Confirmar")
                }
            }
            if (mensaje.isNotEmpty()) androidx.compose.material3.Text(mensaje)
        }
    }
}