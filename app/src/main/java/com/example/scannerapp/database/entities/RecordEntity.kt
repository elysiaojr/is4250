package com.example.scannerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey


// Entity: Record
// INSERT into Record (recordId, recordDate, recordQuantityTaken, recordRemarks, recordType, batchId, userId) VALUES (0, "19/05/2023", 0, "Test", "TAKE_OUT", 0, 0)
@Entity(
  tableName = "record",
  foreignKeys = [
    ForeignKey(
      entity = BatchDetails::class,
      parentColumns = arrayOf("batchId"),
      childColumns = arrayOf("batchId"),
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = User::class,
      parentColumns = arrayOf("userId"),
      childColumns = arrayOf("userId"),
      onDelete = ForeignKey.CASCADE
    )
  ]
)
data class Record(
  @PrimaryKey(autoGenerate = true) val recordId: Int,
  val recordDate: String,
  val recordQuantityTaken: Int,
  val recordRemarks: String,
  val recordType: RecordType,
  val batchId: Int,
  val userId: Int
)

enum class RecordType {
  PUT_IN, TAKE_OUT
}
