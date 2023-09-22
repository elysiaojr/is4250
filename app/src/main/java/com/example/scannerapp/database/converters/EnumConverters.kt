package com.example.scannerapp.database.converters

import androidx.room.TypeConverter
import com.example.scannerapp.database.entities.UnitOfMeasurement
import com.example.scannerapp.database.entities.RecordType

// Converting Enums to String, vice versa
class EnumConverters {

  @TypeConverter
  fun fromUnitOfMeasurement(value: UnitOfMeasurement): String {
    return value.name
  }

  @TypeConverter
  fun toUnitOfMeasurement(value: String): UnitOfMeasurement {
    return UnitOfMeasurement.valueOf(value)
  }

  @TypeConverter
  fun fromRecordType(value: RecordType): String {
    return value.name
  }

  @TypeConverter
  fun toRecordType(value: String): RecordType {
    return RecordType.valueOf(value)
  }

}
