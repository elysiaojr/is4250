package com.example.scannerapp.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index


// Entity: Record
// INSERT into record (recordDate, recordQuantityChanged, recordRemarks, recordType, isActive, batchId, userId) VALUES ("19/05/2023", 5, "Test", "TAKE_OUT", 1, 0, 0)
@Entity(
  tableName = "record",
  indices = [Index(value = ["recordId"], unique = true)],
  foreignKeys = [
    ForeignKey(
      entity = BatchDetails::class,
      parentColumns = arrayOf("batchId"),
      childColumns = arrayOf("batchId"),
//      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = User::class,
      parentColumns = arrayOf("userId"),
      childColumns = arrayOf("userId"),
//      onDelete = ForeignKey.CASCADE
    )
  ]
)
data class Record(
  @PrimaryKey(autoGenerate = true)
  val recordId: Int = 1,
  val recordDate: String,
  val recordQuantityChanged: Int,
  val recordRemarks: String?,
  val recordType: RecordType,
  val isActive: Int,
  val batchId: Int,
  val userId: Int
) : Parcelable {
  constructor(parcel: Parcel) : this (
    parcel.readInt(),
    parcel.readString() ?: "",
    parcel.readInt(),
    parcel.readString() ?: "",
    parcel.readParcelable(RecordType::class.java.classLoader)!!, // Read RecordType from Parcel
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeInt(recordId)
      parcel.writeString(recordDate)
      parcel.writeInt(recordQuantityChanged)
      parcel.writeString(recordRemarks)
      parcel.writeString(recordType.name) // Write RecordType to Parcel
      parcel.writeInt(isActive)
      parcel.writeInt(batchId)
      parcel.writeInt(userId)
    }

    override fun describeContents(): Int {
      return 0
    }

    companion object CREATOR : Parcelable.Creator<Record> {
      override fun createFromParcel(parcel: Parcel): Record {
        return Record(parcel)
      }
      override fun newArray(size: Int): Array<Record?> {
        return arrayOfNulls(size)
      }
    }
  }
enum class RecordType {
  PUT_IN, TAKE_OUT
}
