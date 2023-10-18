package com.example.scannerapp.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Entity: Consumable
@Entity(
  tableName = "consumable",
  indices = [Index(value = ["itemCode"], unique = true)]
)
data class Consumable(
  @PrimaryKey(autoGenerate = true)
  var consumableId: Int = 1,
  val consumableName: String,
  val consumableBrand: String,
  val consumableType: String?, // optional
  val consumableSize: String?, // optional
  val itemCode: String,
  val unitOfMeasurement: UnitOfMeasurement,
  val minimumQuantity: Int,
  val isActive: Int
) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readSerializable() as UnitOfMeasurement,
    parcel.readInt(),
    parcel.readInt(),
    // Initialize other properties here
  )

  // Parameterless constructor
  constructor() : this(
    1, // Default value for consumableId
    "", // Default value for consumableName
    "", // Default value for consumableBrand
    null, // Default value for consumableType
    null, // Default value for consumableSize
    "", // Default value for itemCode
    UnitOfMeasurement.BOX, // Default value for unitOfMeasurement
    0, // Default value for minimumQuantity
    0  // Default value for isActive
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(consumableId)
    parcel.writeString(consumableName)
    parcel.writeString(consumableBrand)
    parcel.writeString(consumableType)
    parcel.writeString(consumableSize)
    parcel.writeString(itemCode)
    parcel.writeSerializable(unitOfMeasurement)
    parcel.writeInt(minimumQuantity)
    parcel.writeInt(isActive)
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

  // New method to concatenate name, brand, type, and size
  fun getConsumableTitle(): String {
    val typeString = consumableType ?: ""
    val sizeString = consumableSize ?: ""
    return "$consumableName,  $consumableBrand, $typeString, $sizeString"
  }
}


enum class UnitOfMeasurement {
  BOX, PC, PACK, CT, BAG
}
