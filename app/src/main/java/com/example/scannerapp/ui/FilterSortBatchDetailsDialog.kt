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
import com.example.scannerapp.dataclass.BatchDetailsFilterSortState
import com.example.scannerapp.dataclass.ConsumableFilterSortState
import com.example.scannerapp.dataclass.SortOrderEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilterSortBatchDetailsDialog : DialogFragment() {
    var onFilterSortAppliedListener: OnFilterSortAppliedListener? = null
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private var currentSortOrder: SortOrderEnum = SortOrderEnum.ASCENDING
    private var currentFilterStatus: Int = 1
    private var currentFilterEmpty: Int = 1
    private var currentFilterExpired: Int = 1


    interface OnFilterSortAppliedListener {
        suspend fun onFilterSortApplied(
            status: Int,
            empty: Int,
            expired: Int,
            sortOrder: SortOrderEnum
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filter_sort_batches, container, false)
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        val filterStatusRadioGroup = view.findViewById<RadioGroup>(R.id.filterStatusRadioGroup)
        val activeRadioButton = view.findViewById<RadioButton>(R.id.activeRadioButton)
        val inactiveRadioButton = view.findViewById<RadioButton>(R.id.inactiveRadioButton)
        val filterEmptyRadioGroup = view.findViewById<RadioGroup>(R.id.filterEmptyRadioGroup)
        val nonEmptyRadioButton = view.findViewById<RadioButton>(R.id.nonEmptyRadioButton)
        val emptyRadioButton = view.findViewById<RadioButton>(R.id.emptyRadioButton)
        val filterExpiryRadioGroup = view.findViewById<RadioGroup>(R.id.filterExpiryRadioGroup)
        val notExpiredRadioButton = view.findViewById<RadioButton>(R.id.notExpiredRadioButton)
        val expiredRadioButton = view.findViewById<RadioButton>(R.id.expiredRadioButton)
        val applyButton = view.findViewById<Button>(R.id.buttonApply)
        val resetButton = view.findViewById<Button>(R.id.buttonReset)
        val sortRadioGroup = view.findViewById<RadioGroup>(R.id.sortRadioGroup)
        val radioSortLastTakeout = view.findViewById<RadioButton>(R.id.radioSortLatestTakeout)
        val radioSortFirstExpiry = view.findViewById<RadioButton>(R.id.radioSortFirstExpiry)
        val radioSortAZ = view.findViewById<RadioButton>(R.id.radioSortAZ)
        val radioSortZA = view.findViewById<RadioButton>(R.id.radioSortZA)

        closeButton.setOnClickListener { dismiss() }

        val args = arguments
        if (args != null) {
            currentFilterStatus = args.getInt("status")
            when (currentFilterStatus) {
                1 -> activeRadioButton.isChecked = true
                0 -> inactiveRadioButton.isChecked = true
            }

            currentFilterEmpty = args.getInt("empty")
            when (currentFilterEmpty) {
                1 -> nonEmptyRadioButton.isChecked = true
                0 -> emptyRadioButton.isChecked = true
            }

            currentFilterExpired = args.getInt("expired")
            when (currentFilterExpired) {
                1 -> notExpiredRadioButton.isChecked = true
                0 -> expiredRadioButton.isChecked = true
            }

            // Retrieve the last selected sorting order and set the corresponding radio button as checked.
            currentSortOrder = args.getSerializable("sortOrder") as SortOrderEnum
            when (currentSortOrder) {
                SortOrderEnum.ASCENDING -> radioSortAZ.isChecked = true
                SortOrderEnum.DESCENDING -> radioSortZA.isChecked = true
                SortOrderEnum.LAST_TAKEOUT -> radioSortLastTakeout.isChecked = true
                SortOrderEnum.FIRST_EXPIRY -> radioSortFirstExpiry.isChecked = true
            }
        }

        applyButton.setOnClickListener {
            val filterStatus = when (filterStatusRadioGroup.checkedRadioButtonId) {
                R.id.activeRadioButton -> 1
                R.id.inactiveRadioButton -> 0
                else -> {1}
            }

            val filterEmpty = when (filterEmptyRadioGroup.checkedRadioButtonId) {
                R.id.nonEmptyRadioButton -> 1
                R.id.emptyRadioButton -> 0
                else -> {1}
            }

            val filterExpiry = when (filterExpiryRadioGroup.checkedRadioButtonId) {
                R.id.notExpiredRadioButton -> 1
                R.id.expiredRadioButton -> 0
                else -> {1}
            }

            // Determine the selected sorting option
            val sortOrder = when (sortRadioGroup.checkedRadioButtonId) {
                R.id.radioSortAZ -> SortOrderEnum.ASCENDING
                R.id.radioSortZA -> SortOrderEnum.DESCENDING
                R.id.radioSortLatestTakeout -> SortOrderEnum.LAST_TAKEOUT
                R.id.radioSortFirstExpiry -> SortOrderEnum.FIRST_EXPIRY
                else -> SortOrderEnum.LAST_TAKEOUT // Default to last takeout if none is selected
            }

            CoroutineScope(Dispatchers.Main).launch {
                onFilterSortAppliedListener?.onFilterSortApplied(filterStatus, filterEmpty, filterExpiry, sortOrder)
            }
            dismiss()
        }

        resetButton.setOnClickListener {
            // Reset the filters
            activeRadioButton.isChecked = true
            nonEmptyRadioButton.isChecked = true
            notExpiredRadioButton.isChecked = true

            // Reset the sort order to latest takeout
            radioSortLastTakeout.isChecked = true
            val sortOrder = SortOrderEnum.LAST_TAKEOUT
        }

        return view
    }

    companion object {
        fun newInstance(batchDetailsFilterSortState: BatchDetailsFilterSortState, sortOrder: SortOrderEnum): FilterSortBatchDetailsDialog {
            val fragment = FilterSortBatchDetailsDialog()
            val args = Bundle()
            args.putInt("status", batchDetailsFilterSortState.status)
            args.putInt("empty", batchDetailsFilterSortState.empty)
            args.putInt("expired", batchDetailsFilterSortState.expired)
            args.putSerializable("sortOrder", sortOrder)
            fragment.arguments = args
            return fragment
        }
    }

}
