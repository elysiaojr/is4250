package com.example.scannerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity: Consumable
// INSERT INTO consumable (consumableName, consumableBrand, consumableType, consumableSize, barcodeId, unitOfMeasurement, perUnitQuantity, minimumQuantity, isActive) VALUES ("Best Syringes", "John & Son", "Type A", "50ml", "BarcodeID01239", "BOX", 50, 10, 1)
@Entity(tableName = "consumable")
data class Consumable(
  @PrimaryKey(autoGenerate = true)
  val consumableId: Int = 1,
  val consumableName: String,
  val consumableBrand: String,
  val consumableType: String?, // optional
  val consumableSize: String?, // optional
  val barcodeId: String,
  val unitOfMeasurement: UnitOfMeasurement,
  val perUnitQuantity: Int,
  val minimumQuantity: Int,
  val isActive: Int
)

enum class UnitOfMeasurement {
  BOX, PIECE, PACK
}
