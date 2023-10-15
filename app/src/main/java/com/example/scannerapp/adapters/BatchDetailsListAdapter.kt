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
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.ui.BatchDetailsActivity
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancelChildren

class BatchDetailsListAdapter(
  private val context: Context,
  private var batchDetailsList: List<BatchDetails>,
  private val batchDetailsViewModel: BatchDetailsViewModel,
  private val adapterJob: CompletableJob = Job(),
  private val adapterScope: CoroutineScope = CoroutineScope(Dispatchers.Main + adapterJob)

) : BaseAdapter(), Filterable {

  private var unfilteredBatchDetailsList = batchDetailsList

  override fun getCount(): Int {
    return batchDetailsList.size
  }

  override fun getItem(position: Int): Any {
    return batchDetailsList[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }


  // Custom method to update the data in the adapter
  fun updateData(newList: List<BatchDetails>) {
    batchDetailsList = newList
    unfilteredBatchDetailsList = newList
    notifyDataSetChanged()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val batchDetail = getItem(position) as BatchDetails
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = inflater.inflate(R.layout.list_item_batch_detail, null)

    val batchNumberTextView = view.findViewById<TextView>(R.id.batchNumber)
    val batchExpiryDateTextView = view.findViewById<TextView>(R.id.batchExpiryDate)
    val batchRemainingQuantityTextView = view.findViewById<TextView>(R.id.batchRemainingQuantity)
    val consumableNameTextView = view.findViewById<TextView>(R.id.consumableName)

    adapterScope.launch {
      val unitOfMeasurement = batchDetailsViewModel.getBatchDetailUOM(batchDetail.consumableId)

      // Once you get the unitOfMeasurement, update the UI on the main thread
      batchRemainingQuantityTextView.text =
        "Remaining: ${batchDetail.batchRemainingQuantity} ${unitOfMeasurement}"
    }

    adapterScope.launch {
      val concatConsumableName =
        batchDetailsViewModel.getBatchDetailConsumableName(batchDetail.consumableId)

      // Once you get the consumableDetails, update the UI on the main thread
      consumableNameTextView.text = "Consumable: ${concatConsumableName}"
    }

    batchNumberTextView.text = batchDetail.batchNumber
    batchExpiryDateTextView.text = "Expiry Date: " + batchDetail.expiryDate

    // Handle clicking into a BatchDetail Item
    val listItemLayout = view.findViewById<ConstraintLayout>(R.id.batch_details_list_item)
    listItemLayout.setOnClickListener {
      // Handle item click here
      val intent = Intent(context, BatchDetailsActivity::class.java)
      intent.putExtra("batchDetail", batchDetail)
      context.startActivity(intent)
    }

    return view
  }

  // for Filter/Sort/Search
  override fun getFilter(): Filter {
    return object : Filter() {
      override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        val filteredList = mutableListOf<BatchDetails>()

        if (constraint.isNullOrBlank()) {
          results.values = unfilteredBatchDetailsList
        } else {
          val filterPattern = constraint.toString().toLowerCase().trim()

          for (item in unfilteredBatchDetailsList) {
            if (item.batchNumber.toLowerCase().contains(filterPattern) ||
              item.expiryDate.toLowerCase().contains(filterPattern)
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
        batchDetailsList = results?.values as List<BatchDetails>
        notifyDataSetChanged()
      }
    }
  }
}

