package com.example.scannerapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.scannerapp.database.entities.PinCode

@Dao
interface PinCodeDao {

  @Query("SELECT pinCode FROM pin_code WHERE id = 1")
  suspend fun getPinCode(): String

  // ... other queries or operations if needed
}
