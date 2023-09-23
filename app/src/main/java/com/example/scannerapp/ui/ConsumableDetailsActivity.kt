package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.scannerapp.R
import com.example.scannerapp.ui.ui.theme.ScannerAppTheme
import com.example.scannerapp.database.entities.Consumable

class ConsumableDetailsActivity : ComponentActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumable_details)

        // Retrieve the selected user's data from the intent extras
        val consumable = intent.getParcelableExtra<Consumable>("consumable")

        val consumableNameTextView = findViewById<TextView>(R.id.consumableNameTextView)
        val consumableBarcodeIdTextView = findViewById<TextView>(R.id.consumableBarcodeIdTextView)
        val consumableCurrentQuantityTextView = findViewById<TextView>(R.id.consumableCurrentQuantityTextView)
        val consumableMinimumQuantityTextView = findViewById<TextView>(R.id.consumableMinimumQuantityTextView)
        val consumableIsGS1BarcodeTextView = findViewById<TextView>(R.id.consumableIsGS1BarcodeTextView)

        // Populate the user's name and status in the TextViews
        consumable?.let {
            consumableNameTextView.text = it.name
            consumableBarcodeIdTextView.text = it.barcodeId
            val currentQuantity = it.currentQuantity.toString() + " " + it.measurementUnit.toString()
            consumableCurrentQuantityTextView.text = currentQuantity
            val minimumQuantity = it.minimumQuantity.toString() + " " + it.measurementUnit.toString()
            consumableMinimumQuantityTextView.text = minimumQuantity
            val isGS1BarcodeParsed = getIsGS1BarcodeText(it.isG1Barcode)
            consumableIsGS1BarcodeTextView.text = isGS1BarcodeParsed
        }

        // Find the back button by its ID
        val backButton = findViewById<ImageView>(R.id.backButton)

        // Set a click listener for the back button
        backButton.setOnClickListener {
            handleOnBackPressed() // This will navigate back to the previous screen
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
}