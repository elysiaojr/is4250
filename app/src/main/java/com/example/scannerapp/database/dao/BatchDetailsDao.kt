package com.example.scannerapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.UnitOfMeasurement

/*
DAOs are responsible for defining methods that access the DB.
We write queries here!
 */
@Dao
interface BatchDetailsDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(batchDetail: BatchDetails)

  @Update
  suspend fun update(batchDetail: BatchDetails)

  @Delete
  suspend fun delete(batchDetail: BatchDetails)

  @Query("SELECT COUNT(*) FROM batch_details WHERE batchNumber = :batchNumber")
  suspend fun countOfBatchDetailsByBatchNumber(batchNumber: String): Int

  @Query("SELECT * FROM batch_details WHERE batchId = :id")
  suspend fun getBatchDetailById(id: Int): BatchDetails

  @Query("SELECT * FROM batch_details WHERE batchNumber = :batchNumber")
  suspend fun getBatchDetailByBatchNumber(batchNumber: String): BatchDetails

  @Query("SELECT * FROM batch_details WHERE batchNumber = :batchNumber")
  suspend fun getBatchDetailsLiveDataByBatchNumber(batchNumber: String): BatchDetails

  @Query("SELECT batch_details.batchId FROM batch_details WHERE batchNumber = :batchNumber")
  suspend fun getBatchIdByBatchNumber(batchNumber: String): Int

  @Query("SELECT batch_details.batchNumber FROM batch_details WHERE batchId = :batchId")
  suspend fun getBatchDetailsNameById(batchId: Int): String

  @Query("SELECT batch_details.expiryDate FROM batch_details WHERE batchId = :batchId")
  suspend fun getBatchExpiryDateById(batchId: Int): String
  @Query("SELECT unitOfMeasurement FROM consumable WHERE consumableId = :id")
  suspend fun getBatchDetailUOM(id: Int): UnitOfMeasurement

  @Query("SELECT consumable.consumableName FROM consumable WHERE consumableId = :id")
  suspend fun getBatchDetailConsumableName(id: Int): String

  @Query("SELECT consumable.consumableBrand FROM consumable WHERE consumableId = :id")
  suspend fun getBatchDetailConsumableBrand(id: Int): String

  @Query("SELECT consumable.consumableType FROM consumable WHERE consumableId = :id")
  suspend fun getBatchDetailConsumableType(id: Int): String

  @Query("SELECT consumable.consumableSize FROM consumable WHERE consumableId = :id")
  suspend fun getBatchDetailConsumableSize(id: Int): String

  @Query("SELECT consumable.consumableName FROM consumable INNER JOIN batch_details ON consumable.consumableId = batch_details.consumableId WHERE batch_details.batchNumber = :batchNumber")
  suspend fun getBatchDetailConsumableNameByBatchNumber(batchNumber: String): String

  @Query("SELECT consumable.consumableBrand FROM consumable INNER JOIN batch_details ON consumable.consumableId = batch_details.consumableId WHERE batch_details.batchNumber = :batchNumber")
  suspend fun getBatchDetailConsumableBrandByBatchNumber(batchNumber: String): String

  @Query("SELECT consumable.consumableType FROM consumable INNER JOIN batch_details ON consumable.consumableId = batch_details.consumableId WHERE batch_details.batchNumber = :batchNumber")
  suspend fun getBatchDetailConsumableTypeByBatchNumber(batchNumber: String): String

  @Query("SELECT consumable.consumableSize FROM consumable INNER JOIN batch_details ON consumable.consumableId = batch_details.consumableId WHERE batch_details.batchNumber = :batchNumber")
  suspend fun getBatchDetailConsumableSizeByBatchNumber(batchNumber: String): String

  @Query(
    """
    SELECT b.*
FROM batch_details AS b
LEFT JOIN record AS r ON b.batchId = r.batchId
GROUP BY b.batchId
ORDER BY
    CASE
        WHEN MAX(SUBSTR(r.recordDate,7,4) || '-' || SUBSTR(r.recordDate,4,2) || '-' || SUBSTR(r.recordDate,1,2)) IS NULL THEN SUBSTR(b.createDate,7,4) || '-' || SUBSTR(b.createDate,4,2) || '-' || SUBSTR(b.createDate,1,2)
        ELSE MAX(SUBSTR(r.recordDate,7,4) || '-' || SUBSTR(r.recordDate,4,2) || '-' || SUBSTR(r.recordDate,1,2))
    END DESC;
"""
  )
  fun getAllBatchDetailsByLatestDate(): LiveData<List<BatchDetails>>


//  @Query(
//    """
//    SELECT b.*
//    FROM batch_details AS b
//    LEFT JOIN consumable AS c ON b.consumableId = c.consumableId
//    GROUP BY b.batchId
//    ORDER BY
//        DATE(SUBSTR(b.expiryDate, 7, 4) || '-' || SUBSTR(b.expiryDate, 4, 2) || '-' || SUBSTR(b.expiryDate, 1, 2)) ASC
//    """
//  )
//  fun getAllBatchDetailsByExpiryDate(): LiveData<List<BatchDetails>>
//
//
//  @Query(
//    """
//    SELECT b.*
//    FROM batch_details AS b
//    LEFT JOIN consumable AS c ON b.consumableId = c.consumableId
//    ORDER BY c.consumableName ASC
//    """
//  )
//  fun getAllBatchDetailsByConsumableNameAsc(): LiveData<List<BatchDetails>>
//
//
//  @Query(
//    """
//    SELECT b.*
//    FROM batch_details AS b
//    LEFT JOIN consumable AS c ON b.consumableId = c.consumableId
//    ORDER BY c.consumableName DESC
//    """
//  )
//  fun getAllBatchDetailsByConsumableNameDesc(): LiveData<List<BatchDetails>>

}

