package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        //val userStatusTextView = findViewById<TextView>(R.id.userStatusTextView)
        // Populate the user's name and status in the TextViews
        consumable?.let {
            consumableNameTextView.text = it.name
            //val statusParsed = getStatusText(it.status)
            //userStatusTextView.text = statusParsed
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
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    ScannerAppTheme {
        Greeting2("Android")
    }
}