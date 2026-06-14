package com.example.appdortarchile20.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evaluaciones")
data class Evaluacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,                  // Mascota involucrada
    val evaluadorEmail: String,      // Quien evalúa
    val evaluadoEmail: String,       // Quien es evaluado
    val estrellas: Int,              // 1 a 5
    val comentario: String = "",
    val timestamp: Long = System.currentTimeMillis()
)