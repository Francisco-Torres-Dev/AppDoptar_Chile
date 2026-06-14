package com.example.appdortarchile20.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mensajes")
data class Mensaje(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,              // ID de la mascota sobre la que se chatea
    val remitenteEmail: String,  // Quien envía
    val destinatarioEmail: String,
    val texto: String,
    val timestamp: Long = System.currentTimeMillis(),
    val leido: Boolean = false
)