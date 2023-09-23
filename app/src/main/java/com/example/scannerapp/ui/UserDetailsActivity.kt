package com.example.scannerapp.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.databinding.ActivityUserDetailsBinding

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityUserDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_details)

        // Retrieve the selected user's data from the intent extras
        val user = intent.getParcelableExtra<User>("user")

        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        val userStatusTextView = findViewById<TextView>(R.id.userStatusTextView)
        // Populate the user's name and status in the TextViews
        user?.let {
            userNameTextView.text = it.name
            val statusParsed = getStatusText(it.status)
            userStatusTextView.text = statusParsed
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

    private fun getStatusText(status: Int): String {
        // You can customize this function to map status values to text
        return when (status) {
            0 -> "Inactive"
            1 -> "Active"
            else -> "Unknown"
        }
    }
}