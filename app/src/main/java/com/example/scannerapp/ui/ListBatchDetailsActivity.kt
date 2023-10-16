package com.example.scannerapp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.adapters.BatchDetailsListAdapter // Assuming you have an adapter like the ConsumableListAdapter
import com.example.scannerapp.ui.ui.theme.ScannerAppTheme
import com.example.scannerapp.ui.utils.showHide
import com.example.scannerapp.viewmodels.BatchDetailsViewModel // Assuming you have a ViewModel for BatchDetails
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions

class ListBatchDetailsActivity : BaseActivity(R.layout.activity_list_batch_details) {
  private lateinit var batchDetailsViewModel: BatchDetailsViewModel
  private lateinit var batchDetailsListView: ListView
  private lateinit var searchView: SearchView
  private lateinit var searchButton: Button
  private lateinit var adapter: BatchDetailsListAdapter

  private val barcodeLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == RESULT_OK) {
        val data = result.data
        val scanResult = data?.getStringExtra("SCAN_RESULT")
        if (scanResult == null) {
          Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(this, "Scanned: $scanResult", Toast.LENGTH_LONG).show();
          val dialogFragment = CreateBatchDetailsDialog()
          val bundle = Bundle()
          bundle.putString("scannedData", scanResult)
          dialogFragment.arguments = bundle
          dialogFragment.show(supportFragmentManager, "CreateBatchDetailsDialog")
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

    // Create the adapter and set it initially
    adapter = BatchDetailsListAdapter(
      this,
      emptyList(),
      batchDetailsViewModel,
    )
    batchDetailsListView.adapter = adapter

    // Observe the LiveData and update the adapter when data changes
    batchDetailsViewModel.allBatchDetails.observe(this, Observer { batchDetails ->
      adapter.updateBatchDetailsData(batchDetails)
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

    // Floating Action Button (if you have one for adding new BatchDetails)
    val fab = findViewById<FloatingActionButton>(R.id.fab_batch_details)
    fab.setOnClickListener {
      val dialogFragment =
        CreateBatchDetailsDialog() // Assuming you have a dialog for creating new BatchDetails
      dialogFragment.show(supportFragmentManager, "CreateBatchDetailsDialog")
    }

    val fabBarcode = findViewById<FloatingActionButton>(R.id.fab_batch_details_barcode)
    fabBarcode.setOnClickListener {
      val integrator = IntentIntegrator(this)
      integrator.captureActivity = VerticalBarcodeScanner::class.java
      integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
      integrator.setPrompt("Scan batch barcode")
      integrator.setOrientationLocked(false)

      val intent = integrator.createScanIntent()
      barcodeLauncher.launch(intent)

//      val scanOptions = ScanOptions() // Customize scan options as needed
//
//      val intent = Intent(this, CaptureActivity::class.java)
//      intent.action = "com.google.zxing.client.android.SCAN"
//      barcodeLauncher.launch(intent)
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
}

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
