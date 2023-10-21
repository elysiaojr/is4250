package com.example.scannerapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.scannerapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

// other activity pages that need the navigation bar can extend from this BaseActivity abstract class
abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupBottomNavigation()
  }

  private fun setupBottomNavigation() {
    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

    bottomNavigationView.setOnItemSelectedListener { item ->
      println("from BA: insideSetOnItemSelectedListener")
      when (item.itemId) {
        R.id.item_consumables -> {
          println("from BA: to consumables")
          // Respond to navigation item 1 click
          if (javaClass != ListConsumablesActivity::class.java) {
            startActivity(Intent(this, ListConsumablesActivity::class.java))

          }
          true
        }

        R.id.item_batch_details -> {
          println("from BA: to batch")
          // Respond to navigation item 2 click
          if (javaClass != ListBatchDetailsActivity::class.java) {
            startActivity(Intent(this, ListBatchDetailsActivity::class.java))
            // navigationView.selectedItemId = R.id.item_batches
          }
          true
        }

        R.id.item_records -> {
          println("from BA: to records")
          // Respond to navigation item 3 click
          if (javaClass != ListRecordsActivity::class.java) {
            startActivity(Intent(this, ListRecordsActivity::class.java))
            // navigationView.selectedItemId = R.id.item_records
          }
          true
        }

        R.id.main_landing -> {
          println("from BA: to main")
          // Respond to navigation item 4 click
          if (javaClass != MainLandingActivity::class.java) {
            startActivity(Intent(this, MainLandingActivity::class.java))
            // navigationView.selectedItemId = R.id.item_settings
          }
          true
        }

        R.id.item_settings -> {
          println("from BA: to settings")
          // Respond to navigation item 4 click
          if (javaClass != SettingsActivity::class.java) {
            startActivity(Intent(this, SettingsActivity::class.java))
          }
          true
        }
//
//        R.id.item_users -> {
//          println("from BA: to users")
//          // Respond to navigation item 4 click
//          if (javaClass != ListUsersActivity::class.java) {
//            startActivity(Intent(this, ListUsersActivity::class.java))
//          }
//          true
//        }

        else -> {
          println("from BA: FALSE")
          false
        }

      }
    }
  }

  protected abstract fun setActiveNavigationItem()

  override fun onResume() {
    super.onResume()
    setActiveNavigationItem()
  }
}
