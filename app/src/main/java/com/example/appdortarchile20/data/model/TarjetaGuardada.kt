package com.example.appdortarchile20.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarjetas")
data class TarjetaGuardada(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String,
    val numeroEnmascarado: String,  // Ej: "**** **** **** 4242"
    val numeroCompleto: String,     // Para autocompletar
    val tipo: String,               // Visa, Mastercard, Amex
    val timestamp: Long = System.currentTimeMillis()
)