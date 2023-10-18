package com.example.scannerapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.scannerapp.R
import androidx.cardview.widget.CardView;
import android.graphics.Color;
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainLandingActivity: BaseActivity(R.layout.activity_main_landing) {
    private lateinit var recordsCardView: CardView
    private lateinit var batchesCardView: CardView
    private lateinit var consumablesCardView: CardView
    private lateinit var archivesButton: View
    private lateinit var settingsButton: View
    private lateinit var buttonsTitle: TextView
    private var archivesMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Record button
        recordsCardView = findViewById(R.id.card_view_records)
        recordsCardView.setOnClickListener {
            val intent = Intent(this, ListRecordsActivity::class.java)
            startActivity(intent)
        }

        // Batch button
        batchesCardView = findViewById(R.id.card_view_batches)
        batchesCardView.setOnClickListener {
            val intent = Intent(this, ListBatchDetailsActivity::class.java)
            startActivity(intent)
        }

        // Consumable button
        consumablesCardView = findViewById(R.id.card_view_consumables)
        consumablesCardView.setOnClickListener {
            val intent = Intent(this, ListConsumablesActivity::class.java)
            startActivity(intent)
        }

        // Settings circular button
        settingsButton = findViewById<ConstraintLayout>(R.id.settings_button)
        settingsButton.setOnClickListener {
            val intent = Intent(this, ListUsersActivity::class.java)
            startActivity(intent)
        }

        // Archives circular button
        buttonsTitle = findViewById<TextView>(R.id.buttons_title)
        archivesButton = findViewById<ConstraintLayout>(R.id.archives_button)
        archivesButton.setOnClickListener {
            handleSwitchArchives()
        }

    }

    private fun handleSwitchArchives() {
        if (archivesMode) {
            buttonsTitle.text = "What do you want to check today?"

            // Change colours
            recordsCardView.setCardBackgroundColor(Color.parseColor("#D9ECFA"));
            batchesCardView.setCardBackgroundColor(Color.parseColor("#FDE7F2"));
            consumablesCardView.setCardBackgroundColor(Color.parseColor("#E5E2FA"));
        } else {
            buttonsTitle.text = "Archives"

            // Change colours
            recordsCardView.setCardBackgroundColor(Color.parseColor("#B8CCDB"));
            batchesCardView.setCardBackgroundColor(Color.parseColor("#D6BCC9"));
            consumablesCardView.setCardBackgroundColor(Color.parseColor("#B9B6CF"));
        }
        archivesMode = !archivesMode
    }
    // for navigation bar
    override fun setActiveNavigationItem() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.main_landing
    }
}