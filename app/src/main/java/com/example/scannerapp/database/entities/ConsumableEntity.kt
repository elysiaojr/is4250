package com.example.scannerapp.database.entities

import android.os.Parcel
import android.os.Parcelable
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
  val isG1Barcode: Int,
  val consumableCategory: ConsumableCategory
  val isActive: Int
) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readSerializable() as MeasurementUnit,
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt(),
    parcel.readSerializable() as ConsumableCategory,
    // Initialize other properties here
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(consumableId)
    parcel.writeString(name)
    parcel.writeString(description)
    parcel.writeString(barcodeId)
    parcel.writeSerializable(measurementUnit)
    parcel.writeInt(currentQuantity)
    parcel.writeInt(minimumQuantity)
    parcel.writeInt(isG1Barcode)
    parcel.writeSerializable(consumableCategory)
    // Write other properties to the parcel
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Consumable> {
    override fun createFromParcel(parcel: Parcel): Consumable {
      return Consumable(parcel)
    }

    override fun newArray(size: Int): Array<Consumable?> {
      return arrayOfNulls(size)
    }
  }
}
  

enum class UnitOfMeasurement {
  BOX, PIECE, PACK
}
