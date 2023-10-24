package com.example.scannerapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.example.scannerapp.viewmodels.PinCodeViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

// This activity displays details of a batch.
class BatchDetailsActivity : AppCompatActivity(),
  EditBatchDetailsDialog.OnBatchDetailsUpdatedListener {

  // Define UI elements and data models.
  private lateinit var batchDetailsViewModel: BatchDetailsViewModel
  private lateinit var pinCodeViewModel: PinCodeViewModel
  private lateinit var batchNumberTextView: TextView
  private lateinit var inactiveStatusTextView: TextView
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

    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
    pinCodeViewModel = ViewModelProvider(this).get(PinCodeViewModel::class.java)

    // Initialize views.
    batchNumberTextView = findViewById(R.id.batchNumberTextView)
    inactiveStatusTextView = findViewById(R.id.inactiveStatus)
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

    // Define and set the action for the edit button.
    val fabEditBatch = findViewById<Button>(R.id.batchEditButton)
    fabEditBatch.setOnClickListener {
      // Open the EditBatchDetailsDialog to edit the batch details.
      val dialogFragment = batchDetail?.let { it1 -> EditBatchDetailsDialog(it1) }
      dialogFragment?.batchDetailsUpdatedListener = this
      dialogFragment?.show(supportFragmentManager, "EditBatchDetailsDialog")
    }
    // val fab = findViewById<Button>(R.id.batchEditButton)
    // fab.setOnClickListener { /* Handle edit click here */ }

    val deleteButton = findViewById<Button>(R.id.batchDeleteButton)

    if ((batchDetail?.isActive ?: Int) == 0) {
      inactiveStatusTextView.visibility = TextView.VISIBLE
      deleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.update_button))
      deleteButton.setCompoundDrawablesWithIntrinsicBounds(
        R.drawable.power_settings_new_24px, 0, 0, 0)
    }

    deleteButton.setOnClickListener {
      showDeleteConfirmationDialog()
    }
  }

  // This method is for showing the deletion dialog with pin code input.
  private fun showDeleteConfirmationDialog() {
    val dialogView = layoutInflater.inflate(R.layout.dialog_pin, null)

    val title = dialogView.findViewById<TextView>(R.id.title)
    val pinInputEditText = dialogView.findViewById<TextInputEditText>(R.id.pinInputEditText)
    val deleteButton = dialogView.findViewById<Button>(R.id.confirm_button)
    val backButton = dialogView.findViewById<Button>(R.id.back_button)

    title.text = "Update Batch"

    if ((batchDetail?.isActive ?: Int) == 1) {

      deleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.delete))
      deleteButton.text = "Update status as Inactive"

      val dialog = AlertDialog.Builder(this)
        .setView(dialogView)
        .create()

      deleteButton.setOnClickListener {
        val enteredPin = pinInputEditText.text.toString()
        verifyPinAndDelete(enteredPin)
        dialog.dismiss()
      }

      backButton.setOnClickListener {
        dialog.dismiss()
      }

      dialog.show()

    } else {

      deleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.update_button))
      deleteButton.text = "Update status as Active"
      deleteButton.setCompoundDrawablesWithIntrinsicBounds(
        R.drawable.power_settings_new_24px, 0, 0, 0)

      val dialog = AlertDialog.Builder(this)
        .setView(dialogView)
        .create()

      deleteButton.setOnClickListener {
        val enteredPin = pinInputEditText.text.toString()
        verifyPinAndActivate(enteredPin)
        dialog.dismiss()
      }

      backButton.setOnClickListener {
        dialog.dismiss()
      }

      dialog.show()
    }
  }

  // This method verifies the pin and deletes the batch if the pin is correct.
  private fun verifyPinAndDelete(enteredPin: String) {
    activityScope.launch {
      val storedPin = withContext(Dispatchers.IO) {
        pinCodeViewModel.getPinCode()
      }
      if (enteredPin == storedPin) {
        deleteBatch()
      } else {
        Toast.makeText(this@BatchDetailsActivity, "Incorrect pin code!", Toast.LENGTH_SHORT)
          .show()
      }
    }
  }

  private fun verifyPinAndActivate(enteredPin: String) {
    activityScope.launch {
      val storedPin = withContext(Dispatchers.IO) {
        pinCodeViewModel.getPinCode()
      }
      if (enteredPin == storedPin) {
        activateBatch()
      } else {
        Toast.makeText(this@BatchDetailsActivity, "Incorrect pin code!", Toast.LENGTH_SHORT)
          .show()
      }
    }
  }

  // This method is for deleting the batch. You might want to adjust it based on how you manage deletion in your batch database.
  private fun deleteBatch() {
    batchDetail?.let { batch ->
      val updatedBatchDetails = batch.copy(isActive = 0)
      activityScope.launch {
        withContext(Dispatchers.IO) {
          batchDetailsViewModel.updateBatchDetails(updatedBatchDetails)
        }
        Toast.makeText(this@BatchDetailsActivity, "Batch updated: inactive", Toast.LENGTH_SHORT)
          .show()
        finish() // Close this activity and return to the previous one.
      }
    }
  }

  private fun activateBatch() {
    batchDetail?.let { batch ->
      val updatedBatchDetails = batch.copy(isActive = 1)
      activityScope.launch {
        withContext(Dispatchers.IO) {
          batchDetailsViewModel.updateBatchDetails(updatedBatchDetails)
        }
        Toast.makeText(this@BatchDetailsActivity, "Batch updated: active", Toast.LENGTH_SHORT)
          .show()
        finish() // Close this activity and return to the previous one.
      }
    }
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

  override fun onBatchDetailsUpdated(updatedBatchDetails: BatchDetails) {
    batchDetail = updatedBatchDetails
    updateUIWithBatchData(updatedBatchDetails)
  }
}
