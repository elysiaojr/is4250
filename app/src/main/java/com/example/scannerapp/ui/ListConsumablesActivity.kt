package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.dataclass.ConsumableFilterSortState
import com.example.scannerapp.dataclass.SortOrderEnum
import com.example.scannerapp.ui.utils.showHide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListConsumablesActivity : BaseActivity(R.layout.activity_list_consumables) {
  private lateinit var consumableViewModel: ConsumableViewModel
  private lateinit var consumableListView: ListView
  private lateinit var searchView: SearchView
  private lateinit var adapter: ConsumableListAdapter
  private lateinit var searchButton: Button
  private lateinit var filteredList: List<Consumable>
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var consumableFilterState = ConsumableFilterSortState(active = false, inactive = false, remainingQuantity = false, sortOrder = SortOrderEnum.ASCENDING)
  private var currentSortOrder: SortOrderEnum = SortOrderEnum.ASCENDING

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
      // Sort the list in ascending alphabetical order (the default option)
      val sortedConsumables = consumables.sortedWith(compareBy (String.CASE_INSENSITIVE_ORDER) { it.consumableName + it.consumableBrand + it.consumableType + it.consumableSize })
      adapter.updateData(sortedConsumables)
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

    // Floating Action Button: Create
    val fab = findViewById<FloatingActionButton>(R.id.fab_consumables)
    fab.setOnClickListener {
      val dialogFragment = CreateConsumableDialog()
      dialogFragment.show(supportFragmentManager, "CreateConsumableDialog")
    }

    // Initialize the filter state
    consumableFilterState = ConsumableFilterSortState(active = true, inactive = true, remainingQuantity = false, sortOrder = currentSortOrder)

    // Apply the filter to the default state
    updateList(consumableFilterState.active, consumableFilterState.inactive, consumableFilterState.remainingQuantity)

    // filter button
    val filterButton = findViewById<Button>(R.id.consumableListFilterButton)
    filterButton.setOnClickListener {
      showFilterSortDialog()
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

  // handle the filter/sort dialog
  private fun showFilterSortDialog() {
    val dialogFragment = FilterSortConsumableDialog.newInstance(consumableFilterState, currentSortOrder)
    dialogFragment.onFilterSortAppliedListener = object : FilterSortConsumableDialog.OnFilterSortAppliedListener {
      override suspend fun onFilterSortApplied(
        active: Boolean,
        inactive: Boolean,
        remainingQuantity: Boolean,
        sortOrder: SortOrderEnum
      ) {
        updateList(active, inactive, remainingQuantity)
        currentSortOrder = sortOrder // Update the sorting order
        saveLastSelectedSortOrder(sortOrder) // Save the last selected sorting order
        consumableFilterState = ConsumableFilterSortState(active, inactive, remainingQuantity, sortOrder) // Update the filter state
      }
    }
    dialogFragment.show(supportFragmentManager, "FilterSortDialogFragment")
  }


  // update list based on filters and sorting order
  private fun updateList(active: Boolean, inactive: Boolean, remainingQuantity: Boolean) {
    CoroutineScope(Dispatchers.Main).launch {
      val allConsumables = consumableViewModel.allConsumables.value.orEmpty()

      // filter the list
      filteredList = allConsumables.filter { consumable ->
        (active || consumable.isActive == 0) &&
                (inactive || consumable.isActive == 1) &&
                (!remainingQuantity || remainingQuantityCheck(consumable))
      }

      // Sort the filtered list based on the current sorting order in a case-insensitive manner
      filteredList = when (currentSortOrder) {
        SortOrderEnum.ASCENDING -> filteredList.sortedWith(compareBy (String.CASE_INSENSITIVE_ORDER) { it.consumableName + it.consumableBrand + it.consumableType + it.consumableSize })
        SortOrderEnum.DESCENDING -> filteredList.sortedWith(compareByDescending (String.CASE_INSENSITIVE_ORDER) { it.consumableName + it.consumableBrand + it.consumableType + it.consumableSize })
      }

      adapter.updateData(filteredList)
    }
  }

  // for filter
  private suspend fun remainingQuantityCheck(consumable: Consumable): Boolean {
    return withContext(Dispatchers.IO) {
      val remainingQuantity = consumableViewModel.getAllBatchesQuantityRemaining(consumable.consumableId)
      return@withContext remainingQuantity < consumable.minimumQuantity
    }
  }

  // Function to save the last selected sorting order
  private fun saveLastSelectedSortOrder(sortOrder: SortOrderEnum) {
    // Use SharedPreferences to store the last selected sorting order
    val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("lastSelectedSortOrder", sortOrder.name)
    editor.apply()
  }

  // Function to retrieve the last selected sorting order
  private fun getLastSelectedSortOrder(): SortOrderEnum {
    val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    val sortOrderName = sharedPreferences.getString("lastSelectedSortOrder", SortOrderEnum.ASCENDING.name)
    return SortOrderEnum.valueOf(sortOrderName ?: SortOrderEnum.ASCENDING.name)
  }

}
