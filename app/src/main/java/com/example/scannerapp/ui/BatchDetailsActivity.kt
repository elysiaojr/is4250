package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// This activity displays details of a batch.
class BatchDetailsActivity : AppCompatActivity() {

  // Define UI elements and data models.
  private lateinit var batchDetailsViewModel: BatchDetailsViewModel
  private lateinit var batchNumberTextView: TextView
  private lateinit var createDateTextView: TextView
  private lateinit var expiryDateTextView: TextView
  private lateinit var batchReceivedQuantityTextView: TextView
  private lateinit var batchRemainingQuantityTextView: TextView
  private lateinit var batchConsumableTextView: TextView
  private lateinit var createRecordButton: Button

  private var batchDetail: BatchDetails? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_batch_details)

    // Retrieve the selected batch detail from the intent.
    batchDetail = intent.getParcelableExtra("batchDetail")

    // Initialize views.
    batchNumberTextView = findViewById(R.id.batchNumberTextView)
    createDateTextView = findViewById(R.id.createDateTextView)
    expiryDateTextView = findViewById(R.id.expiryDateTextView)
    batchReceivedQuantityTextView = findViewById(R.id.batchReceivedQuantityTextView)
    batchRemainingQuantityTextView = findViewById(R.id.batchRemainingQuantityTextView)
    batchConsumableTextView = findViewById(R.id.batchConsumableTextView)
    createRecordButton = findViewById<Button>(R.id.batchCreateRecordButton)

    // Display the batch details in the UI.
    batchDetail?.let {
      updateUIWithBatchData(it)
    }

    createRecordButton.setOnClickListener {
      val dialogFragment = CreateRecordDialog()
      val bundle = Bundle()
      bundle.putString("scannedData", batchNumberTextView.text.toString()) // batchNumber
      dialogFragment.arguments = bundle
      dialogFragment.show(supportFragmentManager, "CreateRecordDialog")
    }

    // Define and set the action for the back button.
    val backButton = findViewById<ImageView>(R.id.backButton)
    backButton.setOnClickListener {
      handleOnBackPressed() // Navigate back to the previous screen.
    }

    // TODO: Implement and set the action for the edit button if needed.
    // Example:
    // val fab = findViewById<Button>(R.id.batchEditButton)
    // fab.setOnClickListener { /* Handle edit click here */ }
  }

  // Handle the back button press.
  private fun handleOnBackPressed() {
    super.onBackPressed()
  }

  // Convert the status of the isActive to a readable string.
  private fun getIsActiveText(status: Int): String {
    return when (status) {
      0 -> "Inactive"
      1 -> "Active"
      else -> "Unknown"
    }
  }

  // Update the UI views with data from the BatchDetails object.
  private fun updateUIWithBatchData(batchDetail: BatchDetails) {
    batchNumberTextView.text = batchDetail.batchNumber
    createDateTextView.text = batchDetail.createDate
    expiryDateTextView.text = batchDetail.expiryDate

    // Get and display Consumable name
    updateConsumableName(batchDetail.consumableId)
    updateBatchDetailQuantities(batchDetail, batchDetail.consumableId)
  }

  // Method to
  private fun updateConsumableName(consumableId: Int) {
    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
    activityScope.launch {
      val consumableName = withContext(Dispatchers.IO) {
        batchDetailsViewModel.getBatchDetailConsumableName(consumableId)
      }
      batchConsumableTextView.text = consumableName
    }
  }

  private fun updateBatchDetailQuantities(batchDetail: BatchDetails, consumableId: Int) {
    activityScope.launch {
      val UOM = withContext(Dispatchers.IO) {
        batchDetailsViewModel.getBatchDetailUOM(consumableId)
      }
      batchReceivedQuantityTextView.text = batchDetail.batchReceivedQuantity.toString() + " " + UOM
      batchRemainingQuantityTextView.text =
        batchDetail.batchRemainingQuantity.toString() + " " + UOM
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    activityScope.cancel()
  }
}
