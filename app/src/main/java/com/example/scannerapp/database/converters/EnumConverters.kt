package com.example.scannerapp.database.converters

import androidx.room.TypeConverter
import com.example.scannerapp.database.entities.ConsumableCategory
import com.example.scannerapp.database.entities.MeasurementUnit
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.database.entities.StorageLocation

// Converting Enums to String, vice versa
class EnumConverters {

  @TypeConverter
  fun fromConsumableCategory(value: ConsumableCategory): String {
    return value.name
  }

  @TypeConverter
  fun toConsumableCategory(value: String): ConsumableCategory {
    return ConsumableCategory.valueOf(value)
  }

  @TypeConverter
  fun fromMeasurementUnit(value: MeasurementUnit): String {
    return value.name
  }

  @TypeConverter
  fun toMeasurementUnit(value: String): MeasurementUnit {
    return MeasurementUnit.valueOf(value)
  }

  @TypeConverter
  fun fromStorageLocation(value: StorageLocation): String {
    return value.name
  }

  @TypeConverter
  fun toStorageLocation(value: String): StorageLocation {
    return StorageLocation.valueOf(value)
  }

  @TypeConverter
  fun fromRecordType(value: RecordType): String {
    return value.name
  }

  @TypeConverter
  fun toRecordType(value: String): RecordType {
    return RecordType.valueOf(value)
  }

//  @TypeConverter
//  fun fromExpiryDate(value: )
}
