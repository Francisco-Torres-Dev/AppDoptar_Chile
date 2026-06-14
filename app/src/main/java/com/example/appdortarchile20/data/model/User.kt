package com.example.appdortarchile20.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String,
    val region: String,
    val age: Int,
    val email: String,
    val password: String,
    val isAdmin: Boolean = false
)