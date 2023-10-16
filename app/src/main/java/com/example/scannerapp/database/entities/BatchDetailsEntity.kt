package com.example.scannerapp.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

// Entity: BatchDetails
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
  var batchId: Int = 1,
  val batchNumber: String,
  val createDate: String,
  val expiryDate: String,
  val batchReceivedQuantity: Int,
  val batchRemainingQuantity: Int,
  val isActive: Int,
  val consumableId: Int
) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt()
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(batchId)
    parcel.writeString(batchNumber)
    parcel.writeString(createDate)
    parcel.writeString(expiryDate)
    parcel.writeInt(batchReceivedQuantity)
    parcel.writeInt(batchRemainingQuantity)
    parcel.writeInt(isActive)
    parcel.writeInt(consumableId)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<BatchDetails> {
    override fun createFromParcel(parcel: Parcel): BatchDetails {
      return BatchDetails(parcel)
    }

    override fun newArray(size: Int): Array<BatchDetails?> {
      return arrayOfNulls(size)
    }
  }
}
