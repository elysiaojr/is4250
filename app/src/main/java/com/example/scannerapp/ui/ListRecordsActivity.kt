package com.example.scannerapp.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.adapters.BatchDetailsListAdapter
import com.example.scannerapp.adapters.RecordsListAdapter
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.dataclass.BatchDetailsFilterSortState
import com.example.scannerapp.dataclass.RecordFilterSortState
import com.example.scannerapp.dataclass.SortOrderEnum
import com.example.scannerapp.ui.utils.showHide
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.RecordViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext


class ListRecordsActivity : BaseActivity(R.layout.activity_list_records), CoroutineScope {
  private val job = Job()
  private lateinit var batchDetailsViewModel: BatchDetailsViewModel
  private lateinit var recordsViewModel: RecordViewModel
  private lateinit var recordsListView: ListView
  private lateinit var searchView: SearchView
  private lateinit var searchButton: Button
  private lateinit var adapter: RecordsListAdapter
  private val activityScope = CoroutineScope(Dispatchers.Main)

  private lateinit var filteredList: List<Record>
  private var recordFilterSort = RecordFilterSortState(
    active = false,
    inactive = false,
    expired = false,
    record = false,
    sortOrder = SortOrderEnum.ASCENDING
  )
  private var currentSortOrder: SortOrderEnum = SortOrderEnum.LAST_TAKEOUT

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

  private val barcodeLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == RESULT_OK) {
        val data = result.data
        val scanResult = data?.getStringExtra("SCAN_RESULT")
        if (scanResult == null) {
          Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
          activityScope.launch {
            val batchExists = checkIfBatchNumberExists(scanResult)
            if (batchExists) {
              // Call the method here to check for earlier expiry batches.
              val shouldProceed = checkForEarlierExpiryBatches(scanResult)

              if (shouldProceed) {
                Toast.makeText(this@ListRecordsActivity, "Scanned: $scanResult", Toast.LENGTH_LONG)
                  .show()
                val dialogFragment = CreateRecordDialog()
                val bundle = Bundle()
                bundle.putString("scannedData", scanResult)
                dialogFragment.arguments = bundle
                dialogFragment.show(supportFragmentManager, "CreateRecordDialog")
              }
              
            } else {
              Toast.makeText(
                this@ListRecordsActivity,
                "Batch with batch number $scanResult does not exist.",
                Toast.LENGTH_LONG
              ).show()
              val dialogFragment = NoExistingBatchDialogFragment()
              val bundle = Bundle()
              bundle.putString("scannedData", scanResult)
              dialogFragment.arguments = bundle
              dialogFragment.show(supportFragmentManager, "NoExistingBatchDialogFragment")
            }
          }

        }
      } else {
        // Handle the case when scanning was canceled or failed.
        Toast.makeText(this, "Barcode scanning cancelled.", Toast.LENGTH_LONG).show();
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    recordsViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
    recordsListView = findViewById<ListView>(R.id.recordsList)
    searchView = findViewById(R.id.recordsSearchView)
    searchButton = findViewById(R.id.recordsListSearchButton)

    // Initialize the filter state
    recordFilterSort = RecordFilterSortState(
      active = false,
      inactive = false,
      expired = false,
      record = false,
      sortOrder = currentSortOrder
    )

    // Create the adapter and set it initially
    adapter = RecordsListAdapter(this, emptyList(), recordsViewModel, batchDetailsViewModel)
    recordsListView.adapter = adapter

    // Observe the LiveData and update the adapter when data changes
    recordsViewModel.allRecords.observe(this, Observer { records ->

      adapter.updateData(sortRecordsByTakeOutDateDescending(records))
    })
    // Apply the filter to the default state
    updateList(
      recordFilterSort.active,
      recordFilterSort.inactive,
      recordFilterSort.record,
      recordFilterSort.expired
    )

    // Set up the SearchView
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        adapter.filter.filter(newText)
        return false
      }
    })

    // Toggle SearchView visibility
    searchButton.setOnClickListener {
      showHide(searchView, searchButton)
    }

    // Floating Action Button
    val fab = findViewById<CardView>(R.id.fab_records)
    fab.setOnClickListener {
      val dialogFragment = CreateRecordDialog()
      dialogFragment.show(supportFragmentManager, "CreateRecordDialog")
    }

    val fabBarcode = findViewById<CardView>(R.id.fab_records_barcode)
    fabBarcode.setOnClickListener {
      val integrator = IntentIntegrator(this)
      integrator.captureActivity = VerticalBarcodeScanner::class.java
      integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
      integrator.setPrompt("Scan batch barcode")
      integrator.setOrientationLocked(false)

      val intent = integrator.createScanIntent()
      barcodeLauncher.launch(intent)
    }

    val filterButton = findViewById<Button>(R.id.recordsListFilterButton)
    filterButton.setOnClickListener {
      showFilterSortDialog()
    }
  }

  // Composable function for barcode scanning
  @Composable
  private fun BarcodeScanner() {
    val context = this
    Button(
      onClick = {
        val scanOptions = ScanOptions() // Customize scan options as needed
        val intent = Intent(context, CaptureActivity::class.java)
        intent.action = "com.google.zxing.client.android.SCAN"
        barcodeLauncher.launch(intent)
      },
      modifier = Modifier.padding(16.dp)
    ) {
      Text(text = "Scan Barcode")
    }
  }

  private fun showFilterSortDialog() {
    val dialogFragment = FilterSortRecordsDialog.newInstance(recordFilterSort, currentSortOrder)
    dialogFragment.onFilterSortAppliedListener =
      object : FilterSortRecordsDialog.OnFilterSortAppliedListener {
        override suspend fun onFilterSortApplied(
          active: Boolean,
          inactive: Boolean,
          record: Boolean,
          expired: Boolean,
          sortOrder: SortOrderEnum
        ) {
          updateList(active, inactive, record, expired)
          currentSortOrder = sortOrder // Update the sorting order
          //saveLastSelectedSortOrder(sortOrder) // Save the last selected sorting order
          recordFilterSort = RecordFilterSortState(
            active,
            inactive,
            record,
            expired,
            sortOrder
          ) // Update the filter state
        }
      }
    dialogFragment.show(supportFragmentManager, "FilterSortDialogFragment")
  }

  private fun updateList(active: Boolean, inactive: Boolean, record: Boolean, expired: Boolean) {
    CoroutineScope(Dispatchers.Main).launch {
      val list = recordsViewModel.allRecords.value.orEmpty()

      Log.d("currentsortorder", currentSortOrder.toString())

      filteredList = list.filter { record ->
        (record.isActive == 1) || (record.recordQuantityChanged != 0)
      }
      filteredList = when (currentSortOrder) {
        SortOrderEnum.ASCENDING -> filterRecordsByOnlyTakeout(filteredList)
        SortOrderEnum.DESCENDING -> filterRecordsByOnlyPutIn(filteredList)
        SortOrderEnum.LAST_TAKEOUT -> sortRecordsByTakeOutDateDescending(filteredList)
        SortOrderEnum.FIRST_EXPIRY -> sortRecordsByExpiryDateAscending(filteredList)
      }

      adapter.updateData(filteredList)
    }
  }

  private fun filterRecordsByOnlyTakeout(recordList: List<Record>): List<Record> {
    val unfilteredList = recordsViewModel.allRecords.value
    return recordList.filter { it.recordType.equals(RecordType.TAKE_OUT) }
  }

  private fun filterRecordsByOnlyPutIn(recordList: List<Record>): List<Record> {
    val unfilteredList = recordsViewModel.allRecords.value
    return recordList.filter { it.recordType.equals(RecordType.PUT_IN) }
  }

  private fun sortRecordsByTakeOutDateDescending(recordList: List<Record>): List<Record> {
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return recordList.sortedWith(Comparator { record1, record2 ->
      val date1 = LocalDate.parse(record1.recordDate, dateFormat)
      val date2 = LocalDate.parse(record2.recordDate, dateFormat)
      date2.compareTo(date1)
    })
  }

  private suspend fun sortRecordsByExpiryDateAscending(recordList: List<Record>): List<Record> {
    return withContext(Dispatchers.Default) {
      val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      recordList.sortedWith(Comparator { record1, record2 ->
        val date1 = runBlocking {
          LocalDate.parse(
            batchDetailsViewModel.getBatchExpiryDateById(record1.batchId),
            dateFormat
          )
        }
        val date2 = runBlocking {
          LocalDate.parse(
            batchDetailsViewModel.getBatchExpiryDateById(record2.batchId),
            dateFormat
          )
        }
        date1.compareTo(date2)
      })
    }
  }


  // for navigation bar
  companion object {
    fun getIntent(context: Context): Intent {
      return Intent(context, ListRecordsActivity::class.java)
    }
  }

  // for navigation bar
  override fun setActiveNavigationItem() {
    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.selectedItemId = R.id.item_records
  }

  private suspend fun checkIfBatchNumberExists(batchDetailsBatchNumber: String): Boolean {
    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
    return withContext(Dispatchers.IO) {
      batchDetailsViewModel.checkIfBatchNumberExists(batchDetailsBatchNumber)
    }
  }

  private suspend fun checkForEarlierExpiryBatches(scannedBatchNumber: String): Boolean {
    val scannedBatch = withContext(Dispatchers.IO) {
      batchDetailsViewModel.getBatchDetailsLiveDataByBatchNumber(scannedBatchNumber)
    } ?: return true // if null, just return true to proceed

    val earlierBatches = withContext(Dispatchers.IO) {
      val convertedDate = convertToSqlDateFormat(scannedBatch.expiryDate)
      batchDetailsViewModel.getBatchesWithEarlierExpiryDates(
        convertedDate,
        scannedBatch.consumableId
      ) // assuming expiryDate is a property of scannedBatch
    }

    // Logging the content of earlierBatches
    Log.d("EarlierBatches", "Content: $earlierBatches")

    if (earlierBatches.isNotEmpty()) {
      // Sort the list by expiry date
      val sortedEarlierBatches = earlierBatches.sortedBy { it.expiryDate }
      val firstBatch = sortedEarlierBatches.first()

      // Show dialog or toast to inform user
      withContext(Dispatchers.Main) {
        AlertDialog.Builder(this@ListRecordsActivity)
          .setTitle("Attention")
          .setMessage("Please take out batch ${firstBatch.batchNumber} with expiry date ${firstBatch.expiryDate} first.")
          .setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
          }
          .create()
          .show()
      }
      return false // Don't proceed
    }
    return true // Proceed
  }


  fun convertToSqlDateFormat(inputDate: String): String {
    // Input format: dd/MM/yyyy
    // Output format: yyyy-MM-dd
    val parts = inputDate.split("/")
    return "${parts[2]}-${parts[1]}-${parts[0]}"
  }

}
