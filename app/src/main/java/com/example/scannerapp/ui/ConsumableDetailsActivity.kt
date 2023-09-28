package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.scannerapp.R
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.ui.ui.theme.ScannerAppTheme
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.viewmodels.ConsumableViewModel

class ConsumableDetailsActivity : AppCompatActivity(), EditConsumableDialog.OnConsumableUpdatedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var consumableViewModel: ConsumableViewModel
    private lateinit var consumableNameTextView: TextView
    private lateinit var consumableBarcodeIdTextView: TextView
    private lateinit var consumableCurrentQuantityTextView: TextView
    private lateinit var consumableMinimumQuantityTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumable_details)

        // Retrieve the selected user's data from the intent extras
        val consumable = intent.getParcelableExtra<Consumable>("consumable")

        consumableNameTextView = findViewById(R.id.consumableNameTextView)
        consumableBarcodeIdTextView = findViewById(R.id.consumableBarcodeIdTextView)
        consumableCurrentQuantityTextView = findViewById(R.id.consumableCurrentQuantityTextView)
        consumableMinimumQuantityTextView = findViewById(R.id.consumableMinimumQuantityTextView)

        // Populate the user's name and status in the TextViews
        consumable?.let {
            consumableNameTextView.text = it.consumableName
            consumableBarcodeIdTextView.text = it.barcodeId
            val currentQuantity = it.perUnitQuantity.toString() + " " + it.unitOfMeasurement.toString()
            consumableCurrentQuantityTextView.text = currentQuantity
            val minimumQuantity = it.minimumQuantity.toString() + " " + it.unitOfMeasurement.toString()
            consumableMinimumQuantityTextView.text = minimumQuantity
            //val isGS1BarcodeParsed = getIsGS1BarcodeText(it.isG1Barcode)
            //consumableIsGS1BarcodeTextView.text = isGS1BarcodeParsed
        }

        // Find the back button by its ID
        val backButton = findViewById<ImageView>(R.id.backButton)

        // Set a click listener for the back button
        backButton.setOnClickListener {
            handleOnBackPressed() // This will navigate back to the previous screen
        }

        val fab = findViewById<Button>(R.id.consumableEditButton)

        fab.setOnClickListener {
            val dialogFragment = consumable?.let { it1 -> EditConsumableDialog(it1) }
            dialogFragment?.consumableUpdatedListener = this // Set the listener
            dialogFragment?.show(supportFragmentManager, "EditConsumableDialog")
        }
    }

    // Handle the back button press
    private fun handleOnBackPressed() {
        super.onBackPressed()
        // Optionally, you can add additional logic here if needed
    }

    private fun getIsGS1BarcodeText(status: Int): String {
        // You can customize this function to map status values to text
        return when (status) {
            0 -> "No"
            1 -> "Yes"
            else -> "Unknown"
        }
    }

    // for edit consumable: to update the ConsumableDetails activity with edits
    override fun onConsumableUpdated(consumable: Consumable) {

        // Update the UI with the new consumable data here
        consumableNameTextView.text = consumable.consumableName
        consumableBarcodeIdTextView.text = consumable.barcodeId
        val currentQuantity = "${consumable.perUnitQuantity} ${consumable.unitOfMeasurement}"
        consumableCurrentQuantityTextView.text = currentQuantity
        val minimumQuantity = "${consumable.minimumQuantity} ${consumable.unitOfMeasurement}"
        consumableMinimumQuantityTextView.text = minimumQuantity
    }
}