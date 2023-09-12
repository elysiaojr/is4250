package com.example.scannerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.util.Date

// Entity: User
@Entity(tableName = "user")
data class User(
  @PrimaryKey(autoGenerate = true) val userId: Int,
  val name: String
)
