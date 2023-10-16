package com.example.scannerapp.adapters

import android.content.Context
import android.content.Intent
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

class ConsumableSearchAdapter(
  private val context: Context,
  private val consumableListView: Int,
  private var consumableList: List<Consumable>
) : BaseAdapter(), Filterable {

  private var unfilteredConsumableList = consumableList

  override fun getCount(): Int {
    return consumableList.size
  }

  override fun getItem(position: Int): Consumable {
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

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val consumable = getItem(position) as Consumable
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = inflater.inflate(R.layout.dropdown_list_item_consumable, null)

    val consumableNameTextView = view.findViewById<TextView>(R.id.consumableName)
    val consumableItemCodeTextView = view.findViewById<TextView>(R.id.consumableItemCode)

    // Set user data to views
    val consumableTitleDisplay =
      consumable.consumableName + ", " + consumable.consumableBrand + ", " + consumable.consumableType + ", " + consumable.consumableSize
    consumableNameTextView.text = consumableTitleDisplay// Replace with user's name
    val itemCodeDisplay = "No. " + consumable.itemCode
    consumableItemCodeTextView.text = itemCodeDisplay

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

}
