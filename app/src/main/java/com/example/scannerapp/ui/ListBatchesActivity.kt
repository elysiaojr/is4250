package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class ListBatchesActivity : BaseActivity(R.layout.activity_list_batches) {

    private lateinit var batchDetailsViewModel: BatchDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
        batchDetailsViewModel.takeOutFromBatch(1, 25)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ListBatchesActivity::class.java)
        }
    }

    override fun setActiveNavigationItem() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.item_batches
    }
}