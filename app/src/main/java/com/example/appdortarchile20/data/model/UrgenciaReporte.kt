package com.example.appdortarchile20.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urgencias")
data class UrgenciaReporte(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = TipoUrgencia.EXTRAVIADO.name,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val horaReporte: Long = System.currentTimeMillis(),
    val imagenUrl: String? = null,
    val resuelta: Boolean = false,
    val reportadoPor: String = "" // FK → users.email
)

enum class TipoUrgencia(val descripcion: String, val colorHex: Long) {
    EXTRAVIADO("Mascota Perdida", 0xFFFFD700),
    HERIDO("Mascota Herida", 0xFFEF5350),
    HAMBRE("Necesita Alimento", 0xFF4CAF50),
    MALTRATO("Denuncia Maltrato", 0xFF9C27B0)
}