package com.example.scannerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

// Entity: BatchDetails
// INSERT into batch_details (batchNumber, expiryDate, batchReceivedQuantity, batchRemainingQuantity, isActive, consumableId) VALUES ( "999888", "19/05/2027", 50, 50, 1, 1)
@Entity(
  tableName = "batch_details",
  indices = [Index(value = ["batchNumber"], unique = true)],
  foreignKeys = [ForeignKey(
    entity = Consumable::class,
    parentColumns = arrayOf("consumableId"),
    childColumns = arrayOf("consumableId"),
//    onDelete = ForeignKey.CASCADE
  )]
)
data class BatchDetails(
  @PrimaryKey(autoGenerate = true)
  val batchId: Int = 1,
  val batchNumber: String,
  val createDate: String,
  val expiryDate: String,
  val batchReceivedQuantity: Int,
  val batchRemainingQuantity: Int,
  val isActive: Int,
  val consumableId: Int
)
