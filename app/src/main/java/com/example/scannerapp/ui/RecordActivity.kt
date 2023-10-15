package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.viewmodels.RecordViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordActivity : AppCompatActivity() {

    // Define UI elements and data models.
    private lateinit var recordViewModel: RecordViewModel
    private lateinit var recordIDTextView: TextView
    private lateinit var createDateTextView: TextView
    private lateinit var recordTypeTextView: TextView
    private lateinit var recordQuantityTextView: TextView
    private lateinit var batchIDTextView:  TextView
    private lateinit var userIDTextView: TextView
    private lateinit var createRecordButton: Button

    private var record: Record? = null
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        // Retrieve the selected record from the intent.
        record = intent.getParcelableExtra("record")

        // Initialize views.
        recordIDTextView = findViewById(R.id.recordIDTextView)
        createDateTextView = findViewById(R.id.createDateTextView)
        recordTypeTextView = findViewById(R.id.recordTypeTextView)
        recordQuantityTextView = findViewById(R.id.recordQuantityTextView)
        batchIDTextView =  findViewById(R.id.batchIDTextView)
        userIDTextView = findViewById(R.id.userIDTextView)
        createRecordButton = findViewById(R.id.createRecordButton)

        // Display the record details in the UI.
        record?.let {
            updateUIWithRecordData(it)
        }

        createRecordButton.setOnClickListener {
            val dialogFragment = CreateRecordDialog()
            val bundle = Bundle()
            bundle.putString("scannedData", recordIDTextView.text.toString())
            dialogFragment.arguments = bundle
            dialogFragment.show(supportFragmentManager, "CreateRecordDialog")
        }

        // Define and set the action for the back button.
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            handleOnBackpressed() // Navigate back to the previous screen.
        }

        // TO DO: Implement and set action for the edit button for Record.
    }

    private fun handleOnBackpressed() {
        super.onBackPressed()
    }

    // Convert the status of the isActive to a readable String.
    private fun getIsActiveText(status: Int): String {
        return when (status) {
            0 -> "Inactive"
            1 -> "Active"
            else -> "Unknown"
        }
    }

    // Update the UI views with data from the Record object.

    private fun updateUIWithRecordData(record: Record) {
        recordIDTextView.text = record.recordId.toString()
        recordTypeTextView.text = record.recordType.name
        createDateTextView.text = record.recordDate
        recordQuantityTextView.text = record.recordQuantityChanged.toString()
        userIDTextView.text = record.userId.toString()
        batchIDTextView.text = record.batchId.toString()
    }
    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}