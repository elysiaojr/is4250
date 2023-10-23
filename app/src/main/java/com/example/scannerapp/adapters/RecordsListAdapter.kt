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
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.ui.RecordActivity
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
//import com.example.scannerapp.ui.RecordsActivity
import com.example.scannerapp.viewmodels.RecordViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RecordsListAdapter(
  private val context: Context,
  private var recordsList: List<Record>,
  private val recordsViewModel: RecordViewModel,
  private val batchDetailsViewModel: BatchDetailsViewModel,
  private val adapterJob: CompletableJob = Job(),
  private val adapterScope: CoroutineScope = CoroutineScope(Dispatchers.Main + adapterJob)
) : BaseAdapter(), Filterable {

  private var unfilteredRecordsList = recordsList

  override fun getCount(): Int {
    return recordsList.size
  }

  override fun getItem(position: Int): Any {
    return recordsList[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  // Custom method to update the data in the adapter

  fun updateData(newList: List<Record>) {
    recordsList = newList
    unfilteredRecordsList = newList
    notifyDataSetChanged()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val record = getItem(position) as Record
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = inflater.inflate(R.layout.list_item_record, null)
    val recordTypeTextView = view.findViewById<TextView>(R.id.recordType)
    val consumableNameTextView = view.findViewById<TextView>(R.id.consumableName)
    val batchNumberTextView = view.findViewById<TextView>(R.id.batchNumber)
    val quantityChangedTextView = view.findViewById<TextView>(R.id.quantityChanged)
    val expiryDateTextView = view.findViewById<TextView>(R.id.expiryDate)
    val recordDateTextView = view.findViewById<TextView>(R.id.recordDate)


    // What to update for Records
//        adapterScope.launch {
//            val unitOfMeasurement = recordsViewModel
//        }

    val recordType: String = if (record.recordType.equals(RecordType.TAKE_OUT)) {
      "Take Out"
    } else {
      "Return"
    }
    recordTypeTextView.text = recordType

    adapterScope.launch {
      val batchNumber = batchDetailsViewModel.getBatchDetailsNameById(record.batchId)
      val consumableName = batchDetailsViewModel.getBatchDetailConsumableNameByBatchNumber(batchNumber)
      val consumableText = "Consumable Name: " + consumableName
      consumableNameTextView.text = getBoldSpannable(consumableText, "Consumable Name:")
    }

    adapterScope.launch {
      val batchNumber = batchDetailsViewModel.getBatchDetailsNameById(record.batchId)
      val batchNameText = "Batch Number: " + batchNumber
      batchNumberTextView.text = getBoldSpannable(batchNameText, "Batch Number:")
    }

    val quantityChangedText = "Quantity: " + record.recordQuantityChanged.toString()
    quantityChangedTextView.text = getBoldSpannable(quantityChangedText, "Quantity:")

    adapterScope.launch {
      val batchExpiryDate = batchDetailsViewModel.getBatchExpiryDateById(record.batchId)
      val expiryDateText = "Expiry Date: " + batchExpiryDate
      expiryDateTextView.text = getBoldSpannable(expiryDateText, "Expiry Date:")
    }

    val recordDateText = "Record Date: " + record.recordDate
    recordDateTextView.text = getBoldSpannable(recordDateText, "Record Date:")

    // Handle clicking to a Record Item

    val listItemLayout = view.findViewById<ConstraintLayout>(R.id.record_list_item)
    listItemLayout.setOnClickListener {
      val intent = Intent(context, RecordActivity::class.java)
      intent.putExtra("record", record)
      context.startActivity(intent)
    }

    return view
  }

  // for Filter/Sort/Search
  override fun getFilter(): Filter {
    return object : Filter() {
      override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        val filteredList = mutableListOf<Record>()

        if (constraint.isNullOrBlank()) {
          results.values = unfilteredRecordsList
        } else {
          val filterPattern = constraint.toString().toLowerCase().trim()

          runBlocking {
            for (item in unfilteredRecordsList) {
              if (shouldAddItem(item, filterPattern)) {
                filteredList.add(item)
              }
            }
          }
          results.values = filteredList
        }

        return results
      }

      suspend fun shouldAddItem(item: Record, filterPattern: String): Boolean {
        val consumableName = withContext(Dispatchers.IO) {
          batchDetailsViewModel.getBatchDetailConsumableName(item.batchId)
        }
        val batchName = withContext(Dispatchers.IO) {
          batchDetailsViewModel.getBatchDetailsNameById(item.batchId)
        }
        val batchExpiryDate = withContext(Dispatchers.IO) {
          batchDetailsViewModel.getBatchExpiryDateById(item.batchId)
        }
        return consumableName.toLowerCase().contains(filterPattern) ||
                batchName.toLowerCase().contains(filterPattern) ||
                batchExpiryDate.toLowerCase().contains(filterPattern) ||
                item.recordDate.toLowerCase().contains(filterPattern)
      }

      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        @Suppress("UNCHECKED_CAST")
        recordsList = results?.values as List<Record>
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
