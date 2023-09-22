package com.example.scannerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

// Entity: User
@Entity(tableName = "user")
data class User(
  @PrimaryKey(autoGenerate = true)
  val userId: Int = 1,
  val name: String,
  val status: Int
) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readString() ?: "",
    parcel.readInt()
    // Initialize other properties here
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(userId)
    parcel.writeString(name)
    parcel.writeInt(status)
    // Write other properties to the parcel
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<User> {
    override fun createFromParcel(parcel: Parcel): User {
      return User(parcel)
    }

    override fun newArray(size: Int): Array<User?> {
      return arrayOfNulls(size)
    }
  }
}
