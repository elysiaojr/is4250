package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.scannerapp.R
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.viewmodels.ConsumableViewModel
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
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var consumable: Consumable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_consumable_details)

    consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)

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
  }

  // Handle the back button press.
  private fun handleOnBackPressed() {
    super.onBackPressed()
  }

  // Convert the status of the GS1 barcode to a readable string.
  private fun getIsGS1BarcodeText(status: Int): String {
    return when (status) {
      0 -> "No"
      1 -> "Yes"
      else -> "Unknown"
    }
  }

  // Update the UI views with data from the Consumable object.
  private fun updateUIWithConsumableData(consumable: Consumable) {
    consumableNameTextView.text = consumable.consumableName + ", " + consumable.consumableBrand + ", " + consumable.consumableType + ", " + consumable.consumableSize
    consumableItemCodeTextView.text = consumable.itemCode
    val minimumQuantity = "${consumable.minimumQuantity} ${consumable.unitOfMeasurement}"
    consumableMinimumQuantityTextView.text = minimumQuantity
  }

  // Callback function to update the UI after editing a consumable.
  override fun onConsumableUpdated(updatedConsumable: Consumable) {
    consumable = updatedConsumable
    updateUIWithConsumableData(updatedConsumable)
  }
}
