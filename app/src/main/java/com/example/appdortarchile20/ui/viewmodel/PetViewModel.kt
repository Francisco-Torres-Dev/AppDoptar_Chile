package com.example.appdortarchile20.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdortarchile20.data.local.AppDatabase
import com.example.appdortarchile20.data.model.Evaluacion
import com.example.appdortarchile20.data.model.Mensaje
import com.example.appdortarchile20.data.model.Pet
import com.example.appdortarchile20.data.model.TarjetaGuardada
import com.example.appdortarchile20.data.model.UrgenciaReporte
import com.example.appdortarchile20.data.model.User
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).appDao()

    // Estado de Mascotas: Se actualiza automáticamente desde Room
    val allPets: StateFlow<List<Pet>> = dao.getAllPets()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Estado de Reportes de Urgencia
    val allReportes: StateFlow<List<UrgenciaReporte>> = dao.getAllReportes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Estado de Login
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    // Usuario autenticado actualmente
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val user = dao.getUserByEmail(email)
            if (user != null && user.password == pass) {
                _currentUser.value = user
                _loginState.value = LoginState.Success(user)
            } else {
                _loginState.value = LoginState.Error("Correo o contraseña incorrectos")
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            try {
                dao.registerUser(user)
                _currentUser.value = user
                _loginState.value = LoginState.Success(user)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error al registrar: ${e.message}")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginState.Idle
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            dao.updateUser(user)
            _currentUser.value = user
        }
    }

    fun addPet(pet: Pet) {
        viewModelScope.launch {
            dao.addPet(pet)
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch { dao.marcarPetEliminada(pet.id) }
    }

    fun restaurarPet(pet: Pet) {
        viewModelScope.launch { dao.restaurarPet(pet.id) }
    }

    val petsEliminadas: StateFlow<List<Pet>> = dao.getPetsEliminadas()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addReporte(reporte: UrgenciaReporte) {
        viewModelScope.launch {
            dao.insertReporte(reporte)
        }
    }

    fun deleteReporte(reporte: UrgenciaReporte) {
        viewModelScope.launch {
            dao.deleteReporte(reporte)
        }
    }

    fun marcarResuelta(id: Int) {
        viewModelScope.launch {
            dao.marcarResuelta(id)
        }
    }

    // Datos iniciales de ejemplo con REGIONES de Chile
    fun insertDummyData() {
        viewModelScope.launch {
            // Crear usuario admin si no existe
            if (dao.getUserByEmail("admin@appdoptar.cl") == null) {
                dao.registerUser(
                    User(
                        id = 0,
                        name = "Administrador",
                        phone = "+56900000000",
                        region = "Metropolitana",
                        age = 30,
                        email = "admin@appdoptar.cl",
                        password = "admin123",
                        isAdmin = true
                    )
                )
            }
            if (allPets.value.isEmpty()) {
                val dummyPets = listOf(
                    Pet(1, "Firulais", "Perro", "2 años", "Metropolitana", "Santiago",
                        "android.resource://com.example.appdortarchile20/drawable/perro",
                        true, true, "Es muy juguetón y cariñoso. Le encanta correr en el parque.",
                        "Juan Pérez", "+5691234567", "juan@example.com"),
                    Pet(2, "Mimi", "Gato", "6 meses", "Valparaíso", "Viña del Mar",
                        "android.resource://com.example.appdortarchile20/drawable/gato",
                        true, false, "Gatita muy tranquila y sociable. Ideal para departamento.",
                        "María Soto", "+5698765432", "maria@example.com"),
                    Pet(3, "Pipo", "Otro", "1 año", "Biobío", "Concepción",
                        "android.resource://com.example.appdortarchile20/drawable/huron",
                        false, false, "Hurón muy curioso y activo. Busca familia con experiencia.",
                        "Andrés Jara", "+5695554321", "andres@example.com")
                )
                dummyPets.forEach { dao.addPet(it) }
            }
        }
    }

    // --- CHAT ---
    fun getMensajesChat(petId: Int, email1: String, email2: String) =
        dao.getMensajesChat(petId, email1, email2)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getMensajesUsuario(email: String) =
        dao.getMensajesUsuario(email)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val mensajesNoLeidos: StateFlow<Int> = _currentUser
        .flatMapLatest { user ->
            if (user != null) dao.getMensajesNoLeidos(user.email)
            else kotlinx.coroutines.flow.flowOf(0)
        }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun enviarMensaje(petId: Int, destinatarioEmail: String, texto: String) {
        val remitente = _currentUser.value?.email ?: return
        viewModelScope.launch {
            dao.insertMensaje(
                Mensaje(petId = petId, remitenteEmail = remitente,
                    destinatarioEmail = destinatarioEmail, texto = texto)
            )
        }
    }

    fun marcarComoLeidos(petId: Int) {
        val email = _currentUser.value?.email ?: return
        viewModelScope.launch { dao.marcarComoLeidos(petId, email) }
    }

    // --- EVALUACIONES ---
    fun getPromedioEstrellas(email: String) =
        dao.getPromedioEstrellas(email)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun getEvaluacionesUsuario(email: String) =
        dao.getEvaluacionesUsuario(email)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun enviarEvaluacion(petId: Int, evaluadoEmail: String, estrellas: Int, comentario: String) {
        val evaluador = _currentUser.value?.email ?: return
        viewModelScope.launch {
            val existente = dao.getEvaluacionExistente(petId, evaluador)
            if (existente == null) {
                dao.insertEvaluacion(
                    Evaluacion(petId = petId, evaluadorEmail = evaluador,
                        evaluadoEmail = evaluadoEmail, estrellas = estrellas,
                        comentario = comentario)
                )
            }
        }
    }

    suspend fun getNombreUsuario(email: String): String {
        return dao.getUserByEmail(email)?.name ?: email
    }

    suspend fun getTituloReporte(reporteId: Int): String {
        return dao.getReporteById(-reporteId)?.titulo ?: ""
    }
    private val _montosRecaudados = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val montosRecaudados: StateFlow<Map<Int, Int>> = _montosRecaudados

    fun agregarDonacion(campaniaId: Int, monto: Int) {
        val actual = _montosRecaudados.value.toMutableMap()
        actual[campaniaId] = (actual[campaniaId] ?: 0) + monto
        _montosRecaudados.value = actual
    }
    suspend fun getTarjetaGuardada(): TarjetaGuardada? {
        val email = _currentUser.value?.email ?: return null
        return dao.getTarjetaUsuario(email)
    }

    fun guardarTarjeta(numero: String, tipo: String) {
        val email = _currentUser.value?.email ?: return
        val ultimos4 = numero.filter { it.isDigit() }.takeLast(4)
        viewModelScope.launch {
            dao.insertTarjeta(
                TarjetaGuardada(
                    userEmail = email,
                    numeroEnmascarado = "**** **** **** $ultimos4",
                    numeroCompleto = numero,
                    tipo = tipo
                )
            )
        }
    }

    fun eliminarTarjeta() {
        val email = _currentUser.value?.email ?: return
        viewModelScope.launch { dao.deleteTarjetasUsuario(email) }
    }
}