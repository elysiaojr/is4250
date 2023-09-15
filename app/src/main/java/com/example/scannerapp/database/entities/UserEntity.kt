package com.example.scannerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity: User
@Entity(tableName = "user")
data class User(
  @PrimaryKey(autoGenerate = true) val userId: Int,
  val name: String
)
