package com.example.scannerapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

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
        ).createFromAsset("database/ScannerApp.db").build()
        INSTANCE = instance
        return instance
      }
    }
  }
}

