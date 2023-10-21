package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.scannerapp.R
import androidx.cardview.widget.CardView;
import android.graphics.Color;
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity: BaseActivity(R.layout.activity_settings) {
    private lateinit var manageUsersButton: ConstraintLayout
    private lateinit var settingsButton: ConstraintLayout
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backButton = findViewById<ImageView>(R.id.back_icon)
        backButton.setOnClickListener {
            startActivity(Intent(this, MainLandingActivity::class.java))
        }

        manageUsersButton = findViewById(R.id.manage_users_item)
        manageUsersButton.setOnClickListener {
            val intent = Intent(this, ListUsersActivity::class.java)
            startActivity(intent)
        }

        settingsButton = findViewById(R.id.change_pin_item)
        settingsButton.setOnClickListener {
            val intent = Intent(this, EditPinActivity::class.java)
            startActivity(intent)
        }
    }

    // for navigation bar
    override fun setActiveNavigationItem() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.item_settings
    }

    // for navigation bar
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

}