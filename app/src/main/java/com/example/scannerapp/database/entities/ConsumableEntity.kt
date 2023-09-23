package com.example.scannerapp.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity: Consumable
// INSERT INTO consumable (name, description, barcodeId, measurementUnit, currentQuantity, minimumQuantity, isG1Barcode, consumableCategory) VALUES ("Best Syringes", "Best Syringes Made in SG", "010203", "BOXES", 50, 10, 0, "SYRINGES")
@Entity(tableName = "consumable")
data class Consumable(
  @PrimaryKey(autoGenerate = true)
  val consumableId: Int = 1,
  val name: String,
  val description: String,
  val barcodeId: String,
  val measurementUnit: MeasurementUnit,
  val currentQuantity: Int,
  val minimumQuantity: Int,
  val isG1Barcode: Int,
  val consumableCategory: ConsumableCategory
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

enum class ConsumableCategory {
  SYRINGES, VIALS
}

enum class MeasurementUnit {
  BOXES, PIECES
}
