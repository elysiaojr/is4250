package com.example.scannerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity: Consumable
// INSERT INTO Consumable (consumableId, name, description, barcodeId, measurementUnit, currentQuantity, minimumQuantity, isG1Barcode, consumableCategory) VALUES (0, "Best Syringes", "Best Syringes Made in SG", "010203", "BOXES", 50, 10, 0, "SYRINGES")
@Entity(tableName = "consumable")
data class Consumable(
  @PrimaryKey(autoGenerate = true) val consumableId: Int,
  val name: String,
  val description: String,
  val barcodeId: String,
  val measurementUnit: MeasurementUnit,
  val currentQuantity: Int,
  val minimumQuantity: Int,
  val isG1Barcode: Int,
  val consumableCategory: ConsumableCategory
)

enum class ConsumableCategory {
  SYRINGES, VIALS
}

enum class MeasurementUnit {
  BOXES, PIECES
}
