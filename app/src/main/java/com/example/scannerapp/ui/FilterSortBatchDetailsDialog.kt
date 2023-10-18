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


    interface OnFilterSortAppliedListener {
        suspend fun onFilterSortApplied(
            active: Boolean,
            inactive: Boolean,
            nonEmpty: Boolean,
            empty: Boolean,
            expired: Boolean,
            sortOrder: SortOrderEnum
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filter_sort_batches, container, false)
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        val activeCheckBox = view.findViewById<CheckBox>(R.id.activeCheckBox)
        val inactiveCheckBox = view.findViewById<CheckBox>(R.id.inactiveCheckBox)
        val nonEmptyCheckBox = view.findViewById<CheckBox>(R.id.nonEmptyCheckBox)
        val emptyCheckBox = view.findViewById<CheckBox>(R.id.emptyCheckBox)
        val expiredCheckBox = view.findViewById<CheckBox>(R.id.expiredCheckBox)
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
            activeCheckBox.isChecked = args.getBoolean("active", false)
            inactiveCheckBox.isChecked = args.getBoolean("inactive", false)
            nonEmptyCheckBox.isChecked = args.getBoolean("nonEmpty", false)
            emptyCheckBox.isChecked = args.getBoolean("empty", false)
            expiredCheckBox.isChecked = args.getBoolean("expired", false)

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
            val active = activeCheckBox.isChecked
            val inactive = inactiveCheckBox.isChecked
            val nonEmpty = nonEmptyCheckBox.isChecked
            val empty = emptyCheckBox.isChecked
            val expired = expiredCheckBox.isChecked

            // Determine the selected sorting option
            val sortOrder = when (sortRadioGroup.checkedRadioButtonId) {
                R.id.radioSortAZ -> SortOrderEnum.ASCENDING
                R.id.radioSortZA -> SortOrderEnum.DESCENDING
                R.id.radioSortLatestTakeout -> SortOrderEnum.LAST_TAKEOUT
                R.id.radioSortFirstExpiry -> SortOrderEnum.FIRST_EXPIRY
                else -> SortOrderEnum.LAST_TAKEOUT // Default to last takeout if none is selected
            }

            CoroutineScope(Dispatchers.Main).launch {
                onFilterSortAppliedListener?.onFilterSortApplied(active, inactive, nonEmpty, empty, expired, sortOrder)
            }
            dismiss()
        }

        resetButton.setOnClickListener {
            // Reset the checkboxes
            activeCheckBox.isChecked = true
            inactiveCheckBox.isChecked = true
            nonEmptyCheckBox.isChecked = false
            emptyCheckBox.isChecked = false
            expiredCheckBox.isChecked = false
            val active = activeCheckBox.isChecked
            val inactive = inactiveCheckBox.isChecked
            val nonEmpty = nonEmptyCheckBox.isChecked
            val empty = emptyCheckBox.isChecked
            val expired = expiredCheckBox.isChecked

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
            args.putBoolean("active", batchDetailsFilterSortState.active)
            args.putBoolean("inactive", batchDetailsFilterSortState.inactive)
            args.putBoolean("nonEmpty", batchDetailsFilterSortState.nonEmpty)
            args.putBoolean("empty", batchDetailsFilterSortState.empty)
            args.putBoolean("expired", batchDetailsFilterSortState.expired)
            args.putSerializable("sortOrder", sortOrder)
            fragment.arguments = args
            return fragment
        }
    }

}
