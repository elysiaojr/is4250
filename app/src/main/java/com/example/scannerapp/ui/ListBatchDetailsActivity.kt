package com.example.scannerapp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.adapters.BatchDetailsListAdapter // Assuming you have an adapter like the ConsumableListAdapter
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.dataclass.BatchDetailsFilterSortState
import com.example.scannerapp.dataclass.ConsumableFilterSortState
import com.example.scannerapp.dataclass.SortOrderEnum
import com.example.scannerapp.ui.ui.theme.ScannerAppTheme
import com.example.scannerapp.ui.utils.showHide
import com.example.scannerapp.viewmodels.BatchDetailsViewModel // Assuming you have a ViewModel for BatchDetails
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListBatchDetailsActivity : BaseActivity(R.layout.activity_list_batch_details),
  CoroutineScope {
  private val job = Job()
  private lateinit var batchDetailsViewModel: BatchDetailsViewModel
  private lateinit var batchDetailsListView: ListView
  private lateinit var searchView: SearchView
  private lateinit var searchButton: Button
  private lateinit var adapter: BatchDetailsListAdapter
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var showArchives = false
  private lateinit var archivesButton: ConstraintLayout
  private lateinit var archivesButtonIcon: ImageView
  private lateinit var title: TextView

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

  private lateinit var filteredList: List<BatchDetails>
  private var batchDetailsFilterSort = BatchDetailsFilterSortState(active = false, inactive = false, nonEmpty = false, empty = false, expired = false, sortOrder = SortOrderEnum.ASCENDING)
  private var currentSortOrder: SortOrderEnum = SortOrderEnum.LAST_TAKEOUT

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
              Toast.makeText(this@ListBatchDetailsActivity, "Batch $scanResult has already been recorded.", Toast.LENGTH_LONG).show()
              openEditBatchDetailsDialog(scanResult)
            //              val dialogFragment = ExistingBatchDialogFragment()
//              val bundle = Bundle()
//              bundle.putString("scannedData", scanResult)
//              dialogFragment.arguments = bundle
//              dialogFragment.show(supportFragmentManager, "ExistingBatchDialogFragment")
//              intent.putExtra("batchDetail", batchDetail)
//              context.startActivity(intent)

            } else {
              Toast.makeText(this@ListBatchDetailsActivity, "Scanned: $scanResult", Toast.LENGTH_LONG).show();
              val dialogFragment = CreateBatchDetailsDialog()
              val bundle = Bundle()
              bundle.putString("scannedData", scanResult)
              dialogFragment.arguments = bundle
              dialogFragment.show(supportFragmentManager, "CreateBatchDetailsDialog")
            }
          }
        }
      } else {
        // Handle the case when scanning was canceled or failed.
        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
    batchDetailsListView = findViewById<ListView>(R.id.batchDetailsList)
    searchView = findViewById(R.id.batchDetailsSearchView)
    searchButton = findViewById(R.id.batchDetailsListSearchButton)
    archivesButton = findViewById(R.id.archives_button)
    archivesButtonIcon = findViewById(R.id.archives_button_icon)
    title = findViewById(R.id.title)

    // Initialize the filter state
    batchDetailsFilterSort = BatchDetailsFilterSortState(active = false, inactive = false, nonEmpty = true, empty = false, expired = false, sortOrder = currentSortOrder)

    // Apply the filter to the default state
    updateList(batchDetailsFilterSort.active, batchDetailsFilterSort.inactive, batchDetailsFilterSort.nonEmpty, batchDetailsFilterSort.empty, batchDetailsFilterSort.expired)

    // Create the adapter and set it initially
    adapter = BatchDetailsListAdapter(
      this,
      emptyList(),
      batchDetailsViewModel,
    )
    batchDetailsListView.adapter = adapter

    // Observe the LiveData and update the adapter when data changes
    batchDetailsViewModel.allBatchDetails.observe(this, Observer { batchDetails ->
      // For initial rendering, show active batch details only
      var activeBatchDetails = batchDetails.filter { batchDetail ->
        (!showArchives && batchDetail.isActive == 1) || (showArchives && batchDetail.isActive == 0)
      }
      activeBatchDetails = activeBatchDetails.filter { batchDetail ->
        (batchDetailsFilterSort.nonEmpty && batchDetail.batchRemainingQuantity != 0) || (batchDetailsFilterSort.empty && batchDetail.batchRemainingQuantity == 0)
      }
      adapter.updateBatchDetailsData(activeBatchDetails)
    })

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

    // Toggle Archives Button
    archivesButton.setOnClickListener{
      toggleArchives()
    }

    // Floating Action Button (if you have one for adding new BatchDetails)
    val fab = findViewById<CardView>(R.id.fab_batch_details)
    fab.setOnClickListener {
      val dialogFragment =
        CreateBatchDetailsDialog() // Assuming you have a dialog for creating new BatchDetails
      dialogFragment.show(supportFragmentManager, "CreateBatchDetailsDialog")
    }

    val fabBarcode = findViewById<CardView>(R.id.fab_batch_details_barcode)
    fabBarcode.setOnClickListener {
      val integrator = IntentIntegrator(this)
      integrator.captureActivity = VerticalBarcodeScanner::class.java
      integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
      integrator.setPrompt("Scan batch barcode")
      integrator.setOrientationLocked(false)

      val intent = integrator.createScanIntent()
      barcodeLauncher.launch(intent)
    }

    // filter button
    val filterButton = findViewById<Button>(R.id.batchDetailsListFilterButton)
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

  // For navigation bar
  companion object {
    fun getIntent(context: Context): Intent {
      return Intent(context, ListBatchDetailsActivity::class.java)
    }
  }

  // For navigation bar
  override fun setActiveNavigationItem() {
    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.selectedItemId = R.id.item_batch_details
  }

  private suspend fun checkIfBatchNumberExists(batchDetailsBatchNumber: String): Boolean {
    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
    return withContext(Dispatchers.IO) {
      batchDetailsViewModel.checkIfBatchNumberExists(batchDetailsBatchNumber)
    }
  }

  private suspend fun openEditBatchDetailsDialog(batchDetailsBatchNumber: String) {
    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)

    return withContext(Dispatchers.IO) {
      val dialogFragment = ExistingBatchDialogFragment(batchDetailsViewModel.getBatchDetailsLiveDataByBatchNumber(batchDetailsBatchNumber))
      val bundle = Bundle()
      bundle.putString("scannedData", batchDetailsBatchNumber)
      dialogFragment.arguments = bundle
      dialogFragment.show(supportFragmentManager, "ExistingBatchDialogFragment")
    }
  }
  // handle the filter/sort dialog
  private fun showFilterSortDialog() {
    val dialogFragment = FilterSortBatchDetailsDialog.newInstance(batchDetailsFilterSort, currentSortOrder)
    dialogFragment.onFilterSortAppliedListener = object : FilterSortBatchDetailsDialog.OnFilterSortAppliedListener {
      override suspend fun onFilterSortApplied(
        active: Boolean,
        inactive: Boolean,
        nonEmpty: Boolean,
        empty: Boolean,
        expired: Boolean,
        sortOrder: SortOrderEnum
      ) {
        updateList(active, inactive, nonEmpty, empty, expired)
        currentSortOrder = sortOrder // Update the sorting order
        //saveLastSelectedSortOrder(sortOrder) // Save the last selected sorting order
        batchDetailsFilterSort = BatchDetailsFilterSortState(active, inactive, nonEmpty, empty, expired, sortOrder) // Update the filter state
      }
    }
    dialogFragment.show(supportFragmentManager, "FilterSortDialogFragment")
  }


  private fun updateList(active: Boolean, inactive: Boolean, nonEmpty: Boolean, empty: Boolean, expired: Boolean) {
    CoroutineScope(Dispatchers.Main).launch {
      val list =  batchDetailsViewModel.allBatchDetails.value.orEmpty()

      Log.d("currentsortorder", currentSortOrder.toString())
      Log.d("sortedlist", list.toString())

      filteredList = list.filter { batchDetail ->
        ((!showArchives && batchDetail.isActive == 1) ||
                (showArchives && batchDetail.isActive == 0)) && (
                (!nonEmpty && !empty && !expired) ||
                (nonEmpty && batchDetail.batchRemainingQuantity != 0) ||
                (empty && batchDetail.batchRemainingQuantity == 0) ||
                (expired && expiredBatchCheck(batchDetail)))
      }

      // Sort the filtered list based on the current sorting order in a case-insensitive manner
      filteredList = when (currentSortOrder) {
        SortOrderEnum.ASCENDING -> sortBatchDetailsAscending(filteredList)
        SortOrderEnum.DESCENDING -> sortBatchDetailsDescending(filteredList)
        SortOrderEnum.LAST_TAKEOUT -> filteredList
        SortOrderEnum.FIRST_EXPIRY -> sortBatchDetailsByExpiryDateAscending(filteredList)
      }

      adapter.updateBatchDetailsData(filteredList)
    }
  }

  private suspend fun sortBatchDetailsAscending(filteredList: List<BatchDetails>): List<BatchDetails> {
    val comparator = compareBy<Pair<String, BatchDetails>> { it.first.lowercase() }

    val batchDetailsWithNames = filteredList.map { batchDetail ->
      batchDetailsViewModel.getBatchDetailConsumableName(batchDetail.consumableId) to batchDetail
    }

    val sortedBatches = batchDetailsWithNames.sortedWith(comparator)

    return sortedBatches.map { it.second }
  }

  private suspend fun sortBatchDetailsDescending(filteredList: List<BatchDetails>): List<BatchDetails> {
    val comparator = compareByDescending<Pair<String, BatchDetails>> { it.first.lowercase() }

    val batchDetailsWithNames = filteredList.map { batchDetail ->
      batchDetailsViewModel.getBatchDetailConsumableName(batchDetail.consumableId) to batchDetail
    }

    val sortedBatches = batchDetailsWithNames.sortedWith(comparator)

    return sortedBatches.map { it.second }
  }

  private fun sortBatchDetailsByExpiryDateAscending(batchDetailsList: List<BatchDetails>): List<BatchDetails> {
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return batchDetailsList.sortedWith(Comparator { batch1, batch2 ->
      val date1 = LocalDate.parse(batch1.expiryDate, dateFormat)
      val date2 = LocalDate.parse(batch2.expiryDate, dateFormat)
      date1.compareTo(date2)
    })
  }

  private fun expiredBatchCheck(batchDetails: BatchDetails): Boolean {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val expiryDate = LocalDate.parse(batchDetails.expiryDate, formatter)

    return expiryDate.isBefore(currentDate)
  }

  private fun toggleArchives() {
    showArchives = if (showArchives) {
      title.text = "Manage Batches"
      archivesButtonIcon.setImageResource(R.drawable.archives_icon)
      updateList(active = true, inactive = false, batchDetailsFilterSort.nonEmpty, batchDetailsFilterSort.empty, batchDetailsFilterSort.expired)
      false
    } else {
      archivesButtonIcon.setImageResource(R.drawable.baseline_chevron_right_24)
      title.text = "Batches Archives"
      updateList(active = false, inactive = true, batchDetailsFilterSort.nonEmpty, batchDetailsFilterSort.empty, batchDetailsFilterSort.expired)
      true
    }
  }


//  // Function to save the last selected sorting order
//  private fun saveLastSelectedSortOrder(sortOrder: SortOrderEnum) {
//    // Use SharedPreferences to store the last selected sorting order
//    val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
//    val editor = sharedPreferences.edit()
//    editor.putString("lastSelectedSortOrderBatchDetails", sortOrder.name)
//    editor.apply()
//  }
}

//              val bundle = Bundle()
//              bundle.putString("scannedData", scanResult)
//              dialogFragment.arguments = bundle
//              dialogFragment.show(supportFragmentManager, "ExistingBatchDialogFragment")
//              intent.putExtra("batchDetail", batchDetail)
//              context.startActivity(intent)

private fun rotateButtonIcon(button: Button, degrees: Float) {
  val rotation = ObjectAnimator.ofFloat(button, "rotation", degrees)
  rotation.duration = 300 // Adjust the duration as needed
  rotation.interpolator = AccelerateDecelerateInterpolator()
  rotation.start()
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ScannerAppTheme {
    Greeting("Android")
  }
}


