package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails

// This activity displays details of a batch.
class BatchDetailsActivity : AppCompatActivity() {

  // Define UI elements and data models.
  private lateinit var batchNumberTextView: TextView
  private lateinit var createDateTextView: TextView
  private lateinit var expiryDateTextView: TextView
  private lateinit var batchReceivedQuantityTextView: TextView
  private lateinit var batchRemainingQuantityTextView: TextView
  private lateinit var isActiveTextView: TextView
  private lateinit var consumableIdTextView: TextView
  private var batchDetail: BatchDetails? = null

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
    isActiveTextView = findViewById(R.id.isActiveTextView)
    consumableIdTextView = findViewById(R.id.consumableIdTextView)

    // Display the batch details in the UI.
    batchDetail?.let {
      updateUIWithBatchData(it)
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
    expiryDateTextView.text = "Expiry Date: " + batchDetail.expiryDate
    batchReceivedQuantityTextView.text = batchDetail.batchReceivedQuantity.toString()
    batchRemainingQuantityTextView.text = batchDetail.batchRemainingQuantity.toString()
    isActiveTextView.text = getIsActiveText(batchDetail.isActive)
    consumableIdTextView.text = batchDetail.consumableId.toString()
  }
}
