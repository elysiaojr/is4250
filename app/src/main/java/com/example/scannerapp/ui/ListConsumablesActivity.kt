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
import com.example.scannerapp.R
import com.example.scannerapp.adapters.ConsumableListAdapter
import com.example.scannerapp.ui.ui.theme.ScannerAppTheme
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.scannerapp.ui.utils.showHide

class ListConsumablesActivity : BaseActivity(R.layout.activity_list_consumables) {
  private lateinit var consumableViewModel: ConsumableViewModel
  private lateinit var consumableListView: ListView
  private lateinit var searchView: SearchView
  private lateinit var adapter: ConsumableListAdapter
  private lateinit var searchButton: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)
    consumableListView = findViewById<ListView>(R.id.consumablelist)
    searchView = findViewById(R.id.consumablesSearchView)
    searchButton = findViewById(R.id.consumableListSearchButton)

    // Create the adapter and set it initially
    adapter = ConsumableListAdapter(this, emptyList())
    consumableListView.adapter = adapter

    // Observe the LiveData and update the adapter when data changes
    consumableViewModel.allConsumables.observe(this, Observer { consumables ->
      adapter.updateData(consumables)
    })

    // Set up the SearchView
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        return false
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        adapter.filter.filter(newText)
        return false
      }
    })

    // Toggle SearchView visibility
    searchButton.setOnClickListener {
      showHide(searchView, searchButton)
    }

    // Floating Action Button
    val fab = findViewById<FloatingActionButton>(R.id.fab_consumables)
    fab.setOnClickListener {
      val dialogFragment = CreateConsumableDialog()
      dialogFragment.show(supportFragmentManager, "CreateConsumableDialog")
    }
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


  private fun rotateButtonIcon(button: Button, degrees: Float) {
    val rotation = ObjectAnimator.ofFloat(button, "rotation", degrees)
    rotation.duration = 300 // Adjust the duration as needed
    rotation.interpolator = AccelerateDecelerateInterpolator()
    rotation.start()
  }

  @Composable
  fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
      text = "Hello $name!",
      modifier = modifier
    )
  }

  @Preview(showBackground = true)
  @Composable
  fun GreetingPreview() {
    ScannerAppTheme {
      Greeting("Android")
    }
  }
}
