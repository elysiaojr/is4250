package com.example.scannerapp.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.RecordViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class ListRecordsActivity : BaseActivity(R.layout.activity_list_records), CoroutineScope {
    private val job = Job()
    private lateinit var recordsViewModel: RecordViewModel
    private lateinit var batchDetailsViewModel: BatchDetailsViewModel
    private val activityScope = CoroutineScope(Dispatchers.Main)
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
                            Toast.makeText(this@ListRecordsActivity, "Scanned: $scanResult", Toast.LENGTH_LONG).show()
                            val dialogFragment = CreateRecordDialog()
                            val bundle = Bundle()
                            bundle.putString("scannedData", scanResult)
                            dialogFragment.arguments = bundle
                            dialogFragment.show(supportFragmentManager, "CreateRecordDialog")
                        } else {
                            Toast.makeText(this@ListRecordsActivity, "Batch with batch number $scanResult does not exist.", Toast.LENGTH_LONG).show()
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

        // Floating Action Button
        val fab = findViewById<FloatingActionButton>(R.id.fab_records)
        fab.setOnClickListener {
            val dialogFragment = CreateRecordDialog()
            dialogFragment.show(supportFragmentManager, "CreateRecordDialog")
        }
        recordsViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)

        val fabBarcode = findViewById<FloatingActionButton>(R.id.fab_records_barcode)
        fabBarcode.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.captureActivity = VerticalBarcodeScanner::class.java
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
            integrator.setPrompt("Scan batch barcode")
            integrator.setOrientationLocked(false)

            val intent = integrator.createScanIntent()
            barcodeLauncher.launch(intent)
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
}