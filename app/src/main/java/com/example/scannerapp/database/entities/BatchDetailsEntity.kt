package com.example.scannerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

// Entity: BatchDetails
// INSERT into BatchDetails (batchId, batchNumber, expiryDate, batchInitialQuantity, batchRemainingQuantity, storageLocation, consumableId) VALUES (0, "999888", "19/05/2027", 50, 50, "SHELF_1", 0)
@Entity(
  tableName = "batch_details",
  foreignKeys = [ForeignKey(
    entity = Consumable::class,
    parentColumns = arrayOf("consumableId"),
    childColumns = arrayOf("consumableId"),
    onDelete = ForeignKey.CASCADE
  )]
)
data class BatchDetails(
  @PrimaryKey(autoGenerate = true) val batchId: Int,
  val batchNumber: String,
  val expiryDate: String,
  val batchInitialQuantity: Int,
  val batchRemainingQuantity: Int,
  val storageLocation: StorageLocation,
  val consumableId: Int
)

enum class StorageLocation {
  SHELF_1, SHELF_2
}
