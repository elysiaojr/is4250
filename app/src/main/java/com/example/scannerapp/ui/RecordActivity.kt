package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.RecordViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.example.scannerapp.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordActivity : AppCompatActivity() {

    // Define UI elements and data models.
    private lateinit var recordViewModel: RecordViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var batchDetailsViewModel: BatchDetailsViewModel
    private lateinit var consumableViewModel: ConsumableViewModel
    private lateinit var recordIDTextView: TextView
    private lateinit var createDateTextView: TextView
    private lateinit var expiryDateTextView: TextView
    private lateinit var recordTypeTextView: TextView
    private lateinit var recordQuantityTextView: TextView
    private lateinit var batchIDTextView:  TextView
    private lateinit var userIDTextView: TextView
//    private lateinit var createRecordButton: Button

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
        expiryDateTextView = findViewById(R.id.expiryDateTextView)
        recordTypeTextView = findViewById(R.id.recordTypeTextView)
        recordQuantityTextView = findViewById(R.id.recordQuantityTextView)
        batchIDTextView =  findViewById(R.id.batchIDTextView)
        userIDTextView = findViewById(R.id.userIDTextView)
//        createRecordButton = findViewById(R.id.createRecordButton)

        // Display the record details in the UI.
        record?.let {
            updateUIWithRecordData(it)
        }

        // Define and set the action for the back button.
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            handleOnBackpressed() // Navigate back to the previous screen.
        }

        // Define and set the action for the Return Record button.
        val fabCreateReturnRecord = findViewById<Button>(R.id.createReturnRecordButton)
        fabCreateReturnRecord.setOnClickListener {
            // Ensure record is not null before launching the coroutine
            record?.let { nonNullRecord ->
                val dialogFragment = CreateReturnRecordDialog(nonNullRecord) // Pass the non-null record to the dialog
                consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)
                batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)

                activityScope.launch {
                    // Retrieve batch number using the non-null record's batchId
                    val tempBatchNumber = withContext(Dispatchers.IO) {
                        batchDetailsViewModel.getBatchDetailsNameById(nonNullRecord.batchId)
                    }
                    // Retrieve consumable name using the retrieved batch number
                    val tempConsumableName = withContext(Dispatchers.IO) {
                        batchDetailsViewModel.getBatchDetailConsumableNameByBatchNumber(tempBatchNumber)
                    }

                    // Create a Bundle and set the retrieved values
                    val bundle = Bundle()
                    bundle.putString("consumableName", tempConsumableName)
                    bundle.putString("batchNumber", tempBatchNumber)

                    // Set the arguments for the dialogFragment
                    dialogFragment.arguments = bundle

                    // Show the dialogFragment
                    dialogFragment.show(supportFragmentManager, "CreateReturnRecordsDialog.kt")
                }
            } ?: run {
                // Handle the case when record is null, if needed
                // For example, show a toast message or log an error
            }
        }
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

        val recordType: String = if (record.recordType.equals(RecordType.TAKE_OUT)) {
            "Take Out"
        } else {
            "Return"
        }

        recordIDTextView.text = record.recordId.toString()
        recordTypeTextView.text = recordType
        createDateTextView.text = record.recordDate
        recordQuantityTextView.text = record.recordQuantityChanged.toString()

        updateUserName(record.userId)
        updateBatchNumberName(record.batchId)
        updateBatchExpiryDate(record.batchId)
    }
    private fun updateUserName(userId: Int) {
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        activityScope.launch {
            val userName = withContext(Dispatchers.IO) {
                userViewModel.getUserNameById(userId)
            }
            userIDTextView.text = userName
        }
    }

    private fun updateBatchExpiryDate(batchId: Int) {
        batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
        activityScope.launch {
            val batchExpiryDate = withContext(Dispatchers.IO) {
                batchDetailsViewModel.getBatchExpiryDateById(batchId)
            }
            expiryDateTextView.text = batchExpiryDate
        }
    }

    private fun updateBatchNumberName(batchId: Int) {
        batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
        activityScope.launch {
            val batchDetailsName = withContext(Dispatchers.IO) {
                batchDetailsViewModel.getBatchDetailsNameById(batchId)
            }
            batchIDTextView.text = batchDetailsName
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}