package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.databinding.ActivityUserDetailsBinding
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userNameTextView: TextView
    private lateinit var userStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        // initialise the ViewModel
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Retrieve the selected user's data from the intent extras
        val user = intent.getParcelableExtra<User>("user")

        userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        userStatusTextView = findViewById<TextView>(R.id.userStatusTextView)
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

        // Show the dialog when the edit button is clicked
        val editUserButton = findViewById<Button>(R.id.userEditButton)
        editUserButton.setOnClickListener {
            showEditUserDialog(user)
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

    private fun showEditUserDialog(user: User?) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_user, null)
        builder.setView(dialogView)

        val textInputEditTextName = dialogView.findViewById<TextInputEditText>(R.id.textInputEditTextName)
        val switchStatus = dialogView.findViewById<MaterialSwitch>(R.id.switchStatus)
        val buttonSave = dialogView.findViewById<MaterialButton>(R.id.buttonSave)
        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)

        // Populate the dialog fields with user details if user is not null
        user?.let {
            textInputEditTextName.setText(it.name)
            switchStatus.isChecked = it.status == 1 // Set the switch based on user status
        }

        val dialog = builder.create()

        buttonSave.setOnClickListener {
            val userName = textInputEditTextName.text.toString().trim()
            val switchStatus = switchStatus.isChecked // true for enabled, false for disabled

            val userStatus: Int = if (switchStatus) {
                1
            } else {
                0
            } // 1 for enabled, 0 for disabled

            if (userName.isNotEmpty()) {

                val newUser = User(userId = 0, name = userName, status = userStatus)

                // use update method in viewModel to update the user
                user?.let {
                    // If user is not null, it means we are editing an existing user
                    newUser.userId = it.userId // Set the user ID to the existing user's ID
                    userViewModel.updateUser(newUser)
                }

                // display success message
                Toast.makeText(
                    this@UserDetailsActivity,
                    if (user != null) "User updated successfully!" else "User created successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                // Update the user details on the screen
                updateUserDetails(newUser)

                dialog.dismiss()

            } else {
                // display an error message if the user name is empty
                Toast.makeText(
                    this@UserDetailsActivity,
                    "Please enter a name.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        closeButton.setOnClickListener {
            // User clicked the close button, dismiss the dialog
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateUserDetails(user: User) {
        userNameTextView.text = user.name
        val statusParsed = getStatusText(user.status)
        userStatusTextView.text = statusParsed
    }

}