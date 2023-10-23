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
import com.example.scannerapp.R
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.example.scannerapp.viewmodels.PinCodeViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// This activity displays details of a consumable.
class ConsumableDetailsActivity : AppCompatActivity(),
  EditConsumableDialog.OnConsumableUpdatedListener {

  // Define UI elements and data models.
  private lateinit var consumableNameTextView: TextView
  private lateinit var consumableItemCodeTextView: TextView
  private lateinit var consumableCurrentQuantityTextView: TextView
  private lateinit var consumableMinimumQuantityTextView: TextView
  private lateinit var consumableViewModel: ConsumableViewModel
  private lateinit var pinCodeViewModel: PinCodeViewModel
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var consumable: Consumable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_consumable_details)

    consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)
    pinCodeViewModel = ViewModelProvider(this).get(PinCodeViewModel::class.java)

    // Retrieve the selected consumable from the intent.
    consumable = intent.getParcelableExtra("consumable")

    // Initialize views.
    consumableNameTextView = findViewById(R.id.consumableNameTextView)
    consumableItemCodeTextView = findViewById(R.id.consumableItemCodeTextView)
    consumableCurrentQuantityTextView = findViewById(R.id.consumableCurrentQuantityTextView)
    consumableMinimumQuantityTextView = findViewById(R.id.consumableMinimumQuantityTextView)

    // Display the consumable details in the UI.
    consumable?.let {
      updateUIWithConsumableData(it)

      // Fetch and update the remaining quantity
      activityScope.launch {
        val remainingQuantity = withContext(Dispatchers.IO) {
          consumableViewModel.getAllBatchesQuantityRemaining(it.consumableId)
        }
        consumableCurrentQuantityTextView.text = "$remainingQuantity ${it.unitOfMeasurement}"

      }
    }

    // Define and set the action for the back button.
    val backButton = findViewById<ImageView>(R.id.backButton)
    backButton.setOnClickListener {
      handleOnBackPressed() // Navigate back to the previous screen.
    }

    // Define and set the action for the edit button.
    val fab = findViewById<Button>(R.id.consumableEditButton)
    fab.setOnClickListener {
      // Open the EditConsumableDialog to edit the consumable details.
      val dialogFragment = consumable?.let { it1 -> EditConsumableDialog(it1) }
      dialogFragment?.consumableUpdatedListener = this
      dialogFragment?.show(supportFragmentManager, "EditConsumableDialog")
    }

    val deleteButton = findViewById<Button>(R.id.consumableDeleteButton)

    if ((consumable?.isActive ?: Int) == 0) {
      deleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.update_button))
      deleteButton.setCompoundDrawablesWithIntrinsicBounds(
        R.drawable.power_settings_new_24px, 0, 0, 0)
    }

    deleteButton.setOnClickListener {
      showDeleteConfirmationDialog()
    }
  }

  private fun showDeleteConfirmationDialog() {
    val dialogView = layoutInflater.inflate(R.layout.dialog_pin, null)

    val title = dialogView.findViewById<TextView>(R.id.title)
    val pinInputEditText = dialogView.findViewById<TextInputEditText>(R.id.pinInputEditText)
    val deleteButton = dialogView.findViewById<Button>(R.id.confirm_button)
    val backButton = dialogView.findViewById<Button>(R.id.back_button)

    if ((consumable?.isActive ?: Int) == 1) {
      title.text = "Update Consumable"

      deleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.delete))
      deleteButton.text = "Update status as Inactive"

      val dialog = AlertDialog.Builder(this)
        .setView(dialogView)
        .create()

      deleteButton.setOnClickListener {
        val enteredPin = pinInputEditText.text.toString()
        verifyPinAndDelete(enteredPin)
//        dialog.dismiss()
      }

      backButton.setOnClickListener {
        dialog.dismiss()
      }

      dialog.show()
    } else {
      title.text = "Update Consumable"

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

  private fun verifyPinAndDelete(enteredPin: String) {
    activityScope.launch {
      val storedPin = withContext(Dispatchers.IO) {
        pinCodeViewModel.getPinCode()
      }
      if (enteredPin == storedPin) {
        deleteConsumable()
      } else {
        Toast.makeText(this@ConsumableDetailsActivity, "Incorrect pin code!", Toast.LENGTH_SHORT)
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
        activateConsumable()
      } else {
        Toast.makeText(this@ConsumableDetailsActivity, "Incorrect pin code!", Toast.LENGTH_SHORT)
          .show()
      }
    }
  }

  private fun deleteConsumable() {
    consumable?.let { consumable ->
      val updatedConsumable = consumable.copy(isActive = 0)
      activityScope.launch {
        withContext(Dispatchers.IO) {
          consumableViewModel.updateConsumable(updatedConsumable)
        }
        Toast.makeText(this@ConsumableDetailsActivity, "Consumable updated: inactive", Toast.LENGTH_SHORT)
          .show()
        finish() // Close this activity and return to the previous one.
      }
    }
  }

  private fun activateConsumable() {
    consumable?.let { consumable ->
      val updatedConsumable = consumable.copy(isActive = 1)
      activityScope.launch {
        withContext(Dispatchers.IO) {
          consumableViewModel.updateConsumable(updatedConsumable)
        }
        Toast.makeText(this@ConsumableDetailsActivity, "Consumable updated: active", Toast.LENGTH_SHORT)
          .show()
        finish() // Close this activity and return to the previous one.
      }
    }
  }

  // Handle the back button press.
  private fun handleOnBackPressed() {
    super.onBackPressed()
  }

  // Update the UI views with data from the Consumable object.
  private fun updateUIWithConsumableData(consumable: Consumable) {
    consumableNameTextView.text =
      consumable.consumableName + ", " + consumable.consumableBrand + ", " + consumable.consumableType + ", " + consumable.consumableSize
    consumableItemCodeTextView.text = consumable.itemCode

    consumable?.let {
      // Fetch and update the remaining quantity
      activityScope.launch {
        val remainingQuantity = withContext(Dispatchers.IO) {
          consumableViewModel.getAllBatchesQuantityRemaining(it.consumableId)
        }
        consumableCurrentQuantityTextView.text = "$remainingQuantity ${it.unitOfMeasurement}"

      }
    }

    val minimumQuantity = "${consumable.minimumQuantity} ${consumable.unitOfMeasurement}"
    consumableMinimumQuantityTextView.text = minimumQuantity
  }

  // Callback function to update the UI after editing a consumable.
  override fun onConsumableUpdated(updatedConsumable: Consumable) {
    consumable = updatedConsumable
    updateUIWithConsumableData(updatedConsumable)
  }
}
