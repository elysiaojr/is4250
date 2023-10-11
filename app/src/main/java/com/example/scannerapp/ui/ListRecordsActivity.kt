package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import com.example.scannerapp.R
import com.example.scannerapp.viewmodels.RecordViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListRecordsActivity : BaseActivity(R.layout.activity_list_records) {

    private lateinit var recordsViewModel: RecordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Floating Action Button
        val fab = findViewById<FloatingActionButton>(R.id.fab_records)
        fab.setOnClickListener {
            val dialogFragment = CreateRecordDialog()
            dialogFragment.show(supportFragmentManager, "CreateRecordDialog")
        }
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