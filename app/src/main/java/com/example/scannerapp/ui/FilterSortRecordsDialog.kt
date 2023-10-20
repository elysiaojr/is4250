package com.example.scannerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.scannerapp.R
import com.example.scannerapp.dataclass.RecordFilterSortState
import com.example.scannerapp.dataclass.BatchDetailsFilterSortState
import com.example.scannerapp.dataclass.ConsumableFilterSortState
import com.example.scannerapp.dataclass.SortOrderEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class FilterSortRecordsDialog : DialogFragment() {
    var onFilterSortAppliedListener: OnFilterSortAppliedListener? = null
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private var currentSortOrder: SortOrderEnum = SortOrderEnum.ASCENDING


    interface OnFilterSortAppliedListener {
        suspend fun onFilterSortApplied(
            active: Boolean,
            inactive: Boolean,
            record: Boolean,
            expired: Boolean,
            sortOrder: SortOrderEnum
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filter_sort_records, container, false)
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        val applyButton = view.findViewById<Button>(R.id.buttonApply)
        val resetButton = view.findViewById<Button>(R.id.buttonReset)
        val sortRadioGroup = view.findViewById<RadioGroup>(R.id.sortRadioGroup)
        val radioSortLastTakeout = view.findViewById<RadioButton>(R.id.radioSortLatestTakeout)
        val radioSortFirstExpiry = view.findViewById<RadioButton>(R.id.radioSortFirstExpiry)

        closeButton.setOnClickListener { dismiss() }

        val args = arguments
        if (args != null) {
            // Retrieve the last selected sorting order and set the corresponding radio button as checked.
            currentSortOrder = args.getSerializable("sortOrder") as SortOrderEnum
            when (currentSortOrder) {
                SortOrderEnum.ASCENDING -> radioSortLastTakeout.isChecked = true
                SortOrderEnum.DESCENDING -> radioSortLastTakeout.isChecked = true
                SortOrderEnum.LAST_TAKEOUT -> radioSortLastTakeout.isChecked = true
                SortOrderEnum.FIRST_EXPIRY -> radioSortFirstExpiry.isChecked = true
            }
        }

        applyButton.setOnClickListener {

            val active = false
            val inactive = false
            val record = true
            val expired = false

            // Determine the selected sorting option
            val sortOrder = when (sortRadioGroup.checkedRadioButtonId) {
                R.id.radioSortLatestTakeout -> SortOrderEnum.LAST_TAKEOUT
                R.id.radioSortFirstExpiry -> SortOrderEnum.FIRST_EXPIRY
                else -> SortOrderEnum.LAST_TAKEOUT // Default to last takeout if none is selected
            }

            CoroutineScope(Dispatchers.Main).launch {
                onFilterSortAppliedListener?.onFilterSortApplied(active, inactive, record, expired, sortOrder)
            }
            dismiss()
        }

        resetButton.setOnClickListener {
            // Reset the checkboxes
            // Reset the sort order to latest takeout
            radioSortLastTakeout.isChecked = true
            val sortOrder = SortOrderEnum.LAST_TAKEOUT
        }

        return view
    }

    companion object {
        fun newInstance(recordFilterSortState: RecordFilterSortState, sortOrder: SortOrderEnum): FilterSortRecordsDialog {
            val fragment = FilterSortRecordsDialog()
            val args = Bundle()
            args.putBoolean("active", recordFilterSortState.active)
            args.putBoolean("inactive", recordFilterSortState.inactive)
            args.putBoolean("nonEmpty", recordFilterSortState.record)
            args.putBoolean("expired", recordFilterSortState.expired)
            args.putSerializable("sortOrder", sortOrder)
            fragment.arguments = args
            return fragment
        }
    }

}
