package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.RecordViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListRecordsActivity : BaseActivity(R.layout.activity_list_records) {

    private lateinit var recordsViewModel: RecordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordsViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
//        val newRecord = Record(recordId = 0, recordDate = "28/09/2023", recordQuantityChanged = 25,  recordRemarks = "testing", recordType = RecordType.PUT_IN, isActive = 1,batchId = 1, userId = 1)
//        recordsViewModel.addRecord(newRecord)
    }

    // for navigation bar
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ListRecordsActivity::class.java)
        }
    }

    // for navigation bar
    override fun setActiveNavigationItem() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.item_records
    }
}