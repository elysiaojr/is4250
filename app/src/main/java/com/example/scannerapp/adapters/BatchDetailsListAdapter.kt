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
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.text.toLowerCase
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.ui.BatchDetailsActivity
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BatchDetailsListAdapter(
  private val context: Context,
  private var batchDetailsList: List<BatchDetails>,
  private val batchDetailsViewModel: BatchDetailsViewModel,
  private val adapterJob: CompletableJob = Job(),
  private val adapterScope: CoroutineScope = CoroutineScope(Dispatchers.Main + adapterJob)

) : BaseAdapter(), Filterable {

  private var unfilteredBatchDetailsList = batchDetailsList
  private val activityScope = CoroutineScope(Dispatchers.Main)
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
  fun updateBatchDetailsData(newList: List<BatchDetails>) {
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
    val batchIcon = view.findViewById<ImageView>(R.id.batchDetailsIcon)

    adapterScope.launch {
      val unitOfMeasurement = batchDetailsViewModel.getBatchDetailUOM(batchDetail.consumableId)

      // Once you get the unitOfMeasurement, update the UI on the main thread
      val uomText = "Remaining: ${batchDetail.batchRemainingQuantity} ${unitOfMeasurement}"
      batchRemainingQuantityTextView.text = getBoldSpannable(uomText, "Remaining:")

      // UI: Indicate shortage
      if (batchDetail.batchRemainingQuantity < 1) {
        batchIcon.setColorFilter(
          ContextCompat.getColor(context, R.color.delete),
          android.graphics.PorterDuff.Mode.SRC_IN
        );
        batchRemainingQuantityTextView.setTextColor(ContextCompat.getColor(context, R.color.delete))
      }
    }

    adapterScope.launch {
      val concatConsumableName =
        batchDetailsViewModel.getBatchDetailConsumableName(batchDetail.consumableId)

      // Once you get the consumableDetails, update the UI on the main thread
      val consumableText = "${concatConsumableName}"
      consumableNameTextView.text = consumableText
    }

    val batchNumberText = "Batch Number: ${batchDetail.batchNumber}"
    batchNumberTextView.text = getBoldSpannable(batchNumberText, "Batch Number:")
    val expiryDateText = "Expiry Date: ${batchDetail.expiryDate}"
    batchExpiryDateTextView.text = getBoldSpannable(expiryDateText, "Expiry Date:")

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

          // Use runBlocking to wait for the coroutine to finish
          runBlocking {
            for (item in unfilteredBatchDetailsList) {
              if (shouldAddItem(item, filterPattern)) {
                filteredList.add(item)
              }
            }
          }
          results.values = filteredList
        }

        return results
      }

      suspend fun shouldAddItem(item: BatchDetails, filterPattern: String): Boolean {
        val consumableName = withContext(Dispatchers.IO) {
          batchDetailsViewModel.getBatchDetailConsumableName(item.consumableId)
        }
        return consumableName.toLowerCase().contains(filterPattern) ||
          item.batchNumber.toLowerCase().contains(filterPattern) ||
          item.expiryDate.toLowerCase().contains(filterPattern)
      }

      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        // Add a null-check here before casting
        if (results?.values != null) {
          @Suppress("UNCHECKED_CAST")
          batchDetailsList = results.values as List<BatchDetails>
          notifyDataSetChanged()
        }
      }
    }
  }


  // For bolding row headers
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

