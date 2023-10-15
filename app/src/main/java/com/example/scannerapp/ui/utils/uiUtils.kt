package com.example.scannerapp.ui.utils

import android.view.ViewGroup
import android.widget.Button
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import com.example.scannerapp.R
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

fun showHide(view: SearchView, searchButton: Button) {
  if (view.visibility == SearchView.VISIBLE) {
    // Clear the text inside the SearchView (Optional)
    view.setQuery(
      "",
      false
    ) // The second argument indicates whether to submit the query or not (false to clear only)

    // Make SearchView invisible
    view.visibility = SearchView.INVISIBLE
    // Set the height of the SearchView to 0
    val layoutParams = view.layoutParams as ViewGroup.LayoutParams
    layoutParams.height = 0
    view.layoutParams = layoutParams

    // Change the right drawable of the Button when hiding the SearchView
    searchButton.setCompoundDrawablesWithIntrinsicBounds(
      0,// Replace with the ID of your desired left drawable
      0, // Set 0 for no drawable on the top
      R.drawable.baseline_search_24, // Set 0 for no drawable on the right
      0  // Set 0 for no drawable on the bottom
    )

  } else {
    // Make SearchView visible
    view.visibility = SearchView.VISIBLE
    // Restore the original height (or set a specific height if needed)
    val layoutParams = view.layoutParams as ViewGroup.LayoutParams
    layoutParams.height =
      ViewGroup.LayoutParams.WRAP_CONTENT // You can use specific height if needed
    view.layoutParams = layoutParams

    // Change the right drawable of the Button when hiding the SearchView
    searchButton.setCompoundDrawablesWithIntrinsicBounds(
      0,// Replace with the ID of your desired left drawable
      0, // Set 0 for no drawable on the top
      R.drawable.baseline_close_24, // Set 0 for no drawable on the right
      0  // Set 0 for no drawable on the bottom
    )
  }
}



