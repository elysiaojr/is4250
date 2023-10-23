package com.example.scannerapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.PinCode

@Dao
interface PinCodeDao {

  @Query("SELECT pinCode FROM pin_code WHERE id = 1")
  suspend fun getPinCode(): String

  @Update
  suspend fun update(pinCode: PinCode)

  @Query("SELECT CASE WHEN :enteredPinCode = (SELECT pinCode FROM pin_code WHERE id = 1) THEN 1 ELSE 0 END")
  suspend fun verifyPinCode(enteredPinCode: String): Int

  // ... other queries or operations if needed
}
