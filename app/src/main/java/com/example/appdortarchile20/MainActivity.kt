package com.example.appdortarchile20

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import com.example.appdortarchile20.ui.screens.*
import com.example.appdortarchile20.ui.theme.AppdortarChile20Theme
import com.example.appdortarchile20.ui.viewmodel.PetViewModel
import org.osmdroid.config.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // --- CONFIGURACIÓN PARA OPENSTREETMAP (PLAN B) ---
        // Esto es vital para que el mapa cargue las imágenes (tiles) correctamente.
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        // Identifica tu app ante los servidores de mapas
        Configuration.getInstance().userAgentValue = packageName

        setContent {
            AppdortarChile20Theme {
                val viewModel: PetViewModel = viewModel()

                // Insertamos datos de ejemplo la primera vez
                LaunchedEffect(Unit) {
                    viewModel.insertDummyData()
                }

                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: PetViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = {
            fadeIn(animationSpec = tween(500)) +
                    slideInHorizontally(animationSpec = tween(500)) { it / 4 }
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500)) +
                    slideOutHorizontally(animationSpec = tween(500)) { -it / 4 }
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(500)) +
                    slideInHorizontally(animationSpec = tween(500)) { -it / 4 }
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(500)) +
                    slideOutHorizontally(animationSpec = tween(500)) { it / 4 }
        }
    ) {
        // 1. Pantalla de Login
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("splash") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        // 2. Pantalla de Registro
        composable(
            "register",
            enterTransition = {
                fadeIn(animationSpec = tween(400)) +
                        slideInHorizontally(animationSpec = tween(400)) { it / 3 }
            },
            exitTransition = {
                fadeOut(animationSpec = tween(400)) +
                        slideOutHorizontally(animationSpec = tween(400)) { -it / 3 }
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(400)) +
                        slideOutHorizontally(animationSpec = tween(400)) { it / 3 }
            }
        ) {
            RegisterScreen(
                viewModel = viewModel,
                onRegistered = {
                    navController.navigate("splash") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // 3. Pantalla Splash
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("main_content") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // 4. Contenedor Principal
        composable("main_content") {
            val currentUser by viewModel.currentUser.collectAsState()
            val navBackStack by navController.currentBackStackEntryAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            // Snackbar de bienvenida al entrar
            LaunchedEffect(Unit) {
                val nombre = currentUser?.name?.split(" ")?.firstOrNull() ?: "bienvenido"
                snackbarHostState.showSnackbar(
                    message = "¡Hola, $nombre! 🐾 Bienvenido a AppDoptar Chile",
                    duration = SnackbarDuration.Short
                )
            }

            // Si el usuario pierde la sesión estando en main_content, vuelve al login
            LaunchedEffect(currentUser) {
                if (currentUser == null &&
                    navBackStack?.destination?.route == "main_content") {
                    navController.navigate("login") {
                        popUpTo("main_content") { inclusive = true }
                    }
                }
            }

            Box {
                MainDrawerScreen(
                    viewModel = viewModel,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("main_content") { inclusive = true }
                        }
                    }
                )
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF2D5A3D),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}