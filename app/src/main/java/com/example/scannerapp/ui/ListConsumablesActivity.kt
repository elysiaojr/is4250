package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.scannerapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListConsumablesActivity : BaseActivity(R.layout.activity_list_consumables) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // for navigation bar
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ListConsumablesActivity::class.java)
        }
    }

    // for navigation bar
    override fun setActiveNavigationItem() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.item_consumables
    }
}