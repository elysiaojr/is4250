package com.example.scannerapp.ui

import android.os.Bundle
import com.example.scannerapp.R
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainLandingActivity: BaseActivity(R.layout.activity_main_landing) {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // for navigation bar
    override fun setActiveNavigationItem() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.item_settings
    }
}