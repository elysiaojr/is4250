package com.example.scannerapp.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.BaseAdapter
import android.widget.Filterable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.ui.ConsumableDetailsActivity
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.viewmodels.ConsumableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConsumableListAdapter(
  private val context: Context,
  private var consumableList: List<Consumable>,
  private val consumableViewModel: ConsumableViewModel,
  private var remainingQuantitiesMap: Map<Int, Int> = emptyMap()

) : BaseAdapter(), Filterable {
  private val activityScope = CoroutineScope(Dispatchers.Main)
  private var unfilteredConsumableList = consumableList

  override fun getCount(): Int {
    return consumableList.size
  }

  override fun getItem(position: Int): Any {
    return consumableList[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  // Custom method to update the data in the adapter
  fun updateData(newList: List<Consumable>) {
    consumableList = newList
    unfilteredConsumableList = newList
    notifyDataSetChanged()
  }

  fun updateRemainingQuantities(newMap: Map<Int, Int>) {
    remainingQuantitiesMap = newMap
    notifyDataSetChanged()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val consumable = getItem(position) as Consumable
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = inflater.inflate(R.layout.list_item_consumable, null)

    val consumableNameTextView = view.findViewById<TextView>(R.id.consumableName)
    val consumableItemCodeTextView = view.findViewById<TextView>(R.id.consumableItemCode)
    val consumableRemainingQuantityTextView = view.findViewById<TextView>(R.id.consumableRemainingQuantity)
    val consumableIcon = view.findViewById<ImageView>(R.id.consumableIcon)

    // Set user data to views
    val consumableTitleDisplay =
      consumable.consumableName + ", " + consumable.consumableBrand + ", " + consumable.consumableType + ", " + consumable.consumableSize
    consumableNameTextView.text = consumableTitleDisplay// Replace with user's name
    val itemCodeDisplay = "Item Code: " + consumable.itemCode
    consumableItemCodeTextView.text = getBoldSpannable(itemCodeDisplay, "Item Code: ")

//    // Check if remaining quantities map contains the consumable ID
//    val consumableRemainingQuantity = remainingQuantitiesMap[consumable.consumableId] ?: 0
//    val remainingText = "Remaining: $consumableRemainingQuantity"
//    consumableRemainingQuantityTextView.text = remainingText

    consumable?.let {
      // Fetch and update the remaining quantity
      activityScope.launch {
        val remainingQuantity = withContext(Dispatchers.IO) {
          consumableViewModel.getAllBatchesQuantityRemaining(it.consumableId)
        }
        consumableRemainingQuantityTextView.text = "Remaining: $remainingQuantity ${it.unitOfMeasurement}"
        
        // UI: Indicate shortage
        if (consumable.minimumQuantity > remainingQuantity) {
          consumableIcon.setColorFilter(
            ContextCompat.getColor(context, R.color.delete),
            android.graphics.PorterDuff.Mode.SRC_IN
          );
          consumableRemainingQuantityTextView.setTextColor(ContextCompat.getColor(context, R.color.delete))
        }
      }
    }

    // Handle clicking into a Consumable Item
    val listItemLayout = view.findViewById<ConstraintLayout>(R.id.consumable_list_item)
    listItemLayout.setOnClickListener {
      // Handle item click here
      val intent = Intent(context, ConsumableDetailsActivity::class.java) // like a navigator
      intent.putExtra("consumable", consumable) // Pass the selected user's data
      context.startActivity(intent)
    }

    return view
  }

  // for Filter/Sort/Search
  override fun getFilter(): Filter {
    return object : Filter() {
      override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        val filteredList = mutableListOf<Consumable>()
        Log.d("CONSTRAINT STARTING LIST", unfilteredConsumableList.toString())

        if (constraint.isNullOrBlank()) {
          //filteredList.addAll(consumableList)
          Log.d("CONSTRAINT EMPTY", unfilteredConsumableList.toString())
          results.values = unfilteredConsumableList
        } else {
          Log.d("CONSTRAINT NON EMPTY", unfilteredConsumableList.toString())
          val filterPattern = constraint.toString().toLowerCase().trim()

          for (item in unfilteredConsumableList) {
            if (item.consumableName.toLowerCase().contains(filterPattern) ||
              item.consumableBrand.toLowerCase().contains(filterPattern) ||
              (item.consumableType != null && item.consumableType.toLowerCase()
                .contains(filterPattern)) ||
              (item.consumableType != null && item.consumableSize.toString()
                .toLowerCase().contains(filterPattern))
            ) {
              filteredList.add(item)
            }
          }
          results.values = filteredList
        }

        return results
      }

      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        @Suppress("UNCHECKED_CAST")
        consumableList = results?.values as List<Consumable>
        notifyDataSetChanged()
      }
    }
  }

  private fun getBoldSpannable(fullText: String, boldText: String): SpannableString {
    val spannable = SpannableString(fullText)
    val start = fullText.indexOf(boldText)
    if (start >= 0) {
      spannable.setSpan(
        StyleSpan(Typeface.BOLD),
        start,
        start + boldText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }
    return spannable
  }

}
