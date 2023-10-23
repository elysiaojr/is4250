package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.scannerapp.R
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.database.entities.PinCode
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.PinCodeViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditPinActivity : AppCompatActivity(R.layout.activity_edit_pin) {
    private val job = Job()
    private lateinit var pinCodeViewModel: PinCodeViewModel
    private lateinit var backButton: ImageView
    private lateinit var oldPinText: TextInputEditText
    private lateinit var newPin1Text: TextInputEditText
    private lateinit var newPin2Text: TextInputEditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pinCodeViewModel = ViewModelProvider(this).get(PinCodeViewModel::class.java)

        backButton = findViewById<ImageView>(R.id.back_icon)
        saveButton = findViewById<Button>(R.id.buttonSave)
        oldPinText = findViewById<TextInputEditText>(R.id.pinInputEditTextOld)
        newPin1Text = findViewById<TextInputEditText>(R.id.pinInputEditText1)
        newPin2Text = findViewById<TextInputEditText>(R.id.pinInputEditText2)

        backButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        pinCodeViewModel.errorLiveData.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }

        pinCodeViewModel.editSuccessLiveData.observe(this) { successMessage ->
            Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        saveButton.setOnClickListener {
            val oldPin = oldPinText.text.toString().trim()
            val newPin1 = newPin1Text.text.toString().trim()
            val newPin2 = newPin2Text.text.toString().trim()

            when {
                oldPin.isEmpty() -> Toast.makeText(
                    this,
                    "Please enter the old Pin Code.",
                    Toast.LENGTH_SHORT
                ).show()

                newPin1.isEmpty() -> Toast.makeText(
                    this,
                    "Please enter a new Pin Code.",
                    Toast.LENGTH_SHORT
                ).show()

                newPin2.isEmpty() -> Toast.makeText(
                    this,
                    "Please re-enter the new Pin Code.",
                    Toast.LENGTH_SHORT
                ).show()

                newPin1 != newPin2 -> Toast.makeText(
                    this,
                    "Pin Codes do not match.",
                    Toast.LENGTH_SHORT
                ).show()

                else -> {

                    val newPinCode = PinCode(
                        id = 1,
                        pinCode = newPin1
                    )

                    try {
                        val editSuccess = pinCodeViewModel.updatePinCode(newPinCode, oldPin)
                    } catch (e: Exception) {
                        // a just-in-case Exception catch here
                    }
                }
            }
        }
    }
}