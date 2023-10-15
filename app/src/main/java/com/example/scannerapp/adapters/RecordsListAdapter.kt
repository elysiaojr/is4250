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
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.ui.RecordActivity
//import com.example.scannerapp.ui.RecordsActivity
import com.example.scannerapp.viewmodels.RecordViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancelChildren
class RecordsListAdapter(
    private val context: Context,
    private var recordsList: List<Record>,
    private val recordsViewModel: RecordViewModel,
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

        val recordTextView = view.findViewById<TextView>(R.id.recordNumber)
        val recordDateTextView = view.findViewById<TextView>(R.id.recordDate)

        // What to update for Records
//        adapterScope.launch {
//            val unitOfMeasurement = recordsViewModel
//        }

        recordTextView.text = record.recordId.toString()
        recordDateTextView.text = "Record Date: " + record.recordDate

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

                    for (item in unfilteredRecordsList) {
                        if (item.recordId.toString().contains(filterPattern) ||
                            item.recordDate.toLowerCase().contains(filterPattern)
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
                recordsList = results?.values as List<Record>
                notifyDataSetChanged()
            }
        }
    }
}
