package com.example.scannerapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditUserActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputEditTextName: TextInputEditText
    private lateinit var switchStatus: MaterialSwitch
    private lateinit var buttonSave: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // initialise the ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        textInputLayoutName = findViewById(R.id.textInputLayoutName)
        textInputEditTextName = findViewById(R.id.textInputEditTextName)
        switchStatus = findViewById(R.id.switchStatus)
        buttonSave = findViewById(R.id.buttonSave)

        // retrieve the user data
        val userId = intent.getIntExtra("userId", 1) // TODO: replace the defaultValue with the actual user id
        if (userId != -1) {
            val user = userViewModel.getUserById(userId) // TODO: create getUserById method in UserViewModel
            if (user != null) {
                // populate the UI textfield and switch with the user's current data
//                textInputEditTextName.setText(user.getName())
//                switchStatus.isChecked = user.getStatus()

                buttonSave.setOnClickListener {
                    updateUserToDatabase()
                }
            }
        }
    }

    private fun updateUserToDatabase() {
        val userName = textInputEditTextName.text.toString().trim()
        val switchStatus = switchStatus.isChecked // true for enabled, false for disabled

        val userStatus: Int = if (switchStatus) {
            1
        } else {
            0
        } // 1 for enabled, 0 for disabled

        if (userName.isNotEmpty()) {
            val updatedUser = User(userId = 0, name = userName, status = userStatus)
            userViewModel.updateUser(updatedUser) // TODO: create updateUser method in UserViewModel

            // display success message
            Toast.makeText(
                this@EditUserActivity,
                "User created successfully!",
                Toast.LENGTH_SHORT
            ).show()

        } else {
            // display an error message if the user name is empty
            Toast.makeText(
                this@EditUserActivity,
                "Please enter a name.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}