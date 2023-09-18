package com.example.scannerapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.scannerapp.database.dao.BatchDetailsDao
import com.example.scannerapp.database.dao.ConsumableDao
import com.example.scannerapp.database.converters.DateConverters
import com.example.scannerapp.database.converters.EnumConverters
import com.example.scannerapp.database.dao.RecordDao
import com.example.scannerapp.database.dao.UserDao
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.User

@Database(
  entities = [Consumable::class, BatchDetails::class, Record::class, User::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(EnumConverters::class, DateConverters::class)
abstract class AppDatabase : RoomDatabase() {

  abstract fun consumablesDao(): ConsumableDao

  @TypeConverters(DateConverters::class)
  abstract fun batchDetailsDao(): BatchDetailsDao

  @TypeConverters(DateConverters::class)
  abstract fun recordDao(): RecordDao
  abstract fun userDao(): UserDao

  companion object {
    // Writes to this field are immediately made visible to other threads
    // Singleton class
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      // Check if instance of this DB exists, if exists don't do anything, if does not exist create new instance
      val tempInstance = INSTANCE
      if (tempInstance != null) {
        return tempInstance
      }
      synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "app_database"
        ).build()
        INSTANCE = instance
        return instance
      }
    }
  }
}

