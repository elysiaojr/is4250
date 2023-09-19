package com.example.scannerapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.viewmodels.UserViewModel

class CreateUserActivity : AppCompatActivity() {

  private lateinit var userViewModel: UserViewModel
  private lateinit var editTextName: EditText
  private lateinit var switchStatus: Switch
  private lateinit var buttonSave: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    // link this CreateUserActivity to the UI layout activity_create_user
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_create_user)

    // initialise the ViewModel
    userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

    editTextName = findViewById(R.id.editTextName)
    switchStatus = findViewById(R.id.switchStatus)
    buttonSave = findViewById(R.id.buttonSave)

    // only enable the "Save" button when there is text in the EditText
    editTextName.addTextChangedListener {
      val isNameEmpty = editTextName.text.toString().trim().isEmpty()
      buttonSave.isEnabled = !isNameEmpty
    }

    buttonSave.setOnClickListener {
      saveUserToDatabase()
    }
  }

  private fun saveUserToDatabase() {
    val userName = editTextName.text.toString().trim()
    val switchStatus = switchStatus.isChecked // true for enabled, false for disabled

    val userStatus: Int = if (switchStatus) {
      1
    } else {
      0
    } // 1 for enabled, 0 for disabled

    if (userName.isNotEmpty()) {

      val newUser = User(userId = 0, name = userName, status = userStatus)

      // use the function in ViewModel to add the user
      userViewModel.addUser(newUser)

      // display success message
      Toast.makeText(
        this@CreateUserActivity,
        "User created successfully!",
        Toast.LENGTH_SHORT
      ).show()


    } else {
      // display an error message if the user name is empty
      Toast.makeText(
        this@CreateUserActivity,
        "Please enter a name.",
        Toast.LENGTH_SHORT
      ).show()
    }
  }

}
