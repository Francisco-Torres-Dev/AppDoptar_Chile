package com.example.appdortarchile20.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,
    val age: String,
    val region: String,
    val city: String,
    val imageUrl: String,
    val hasVaccines: Boolean,
    val isSterilized: Boolean,
    val description: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerEmail: String = "",
    val eliminado: Boolean = false  // Borrado
)