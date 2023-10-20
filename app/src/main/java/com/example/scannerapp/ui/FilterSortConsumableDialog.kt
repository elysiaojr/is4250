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
import com.example.scannerapp.dataclass.ConsumableFilterSortState
import com.example.scannerapp.dataclass.SortOrderEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilterSortConsumableDialog : DialogFragment() {
    var onFilterSortAppliedListener: OnFilterSortAppliedListener? = null
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private var currentSortOrder: SortOrderEnum = SortOrderEnum.ASCENDING


    interface OnFilterSortAppliedListener {
        suspend fun onFilterSortApplied(
            active: Boolean,
            inactive: Boolean,
            remainingQuantity: Boolean,
            sortOrder: SortOrderEnum
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filter_sort_consumables, container, false)
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        val activeCheckBox = view.findViewById<CheckBox>(R.id.activeCheckBox)
        val inactiveCheckBox = view.findViewById<CheckBox>(R.id.inactiveCheckBox)
        val remainingQuantityCheckBox = view.findViewById<CheckBox>(R.id.remainingQuantityCheckBox)
        val applyButton = view.findViewById<Button>(R.id.buttonApply)
        val resetButton = view.findViewById<Button>(R.id.buttonReset)
        val sortRadioGroup = view.findViewById<RadioGroup>(R.id.sortRadioGroup)
        val radioSortAZ = view.findViewById<RadioButton>(R.id.radioSortAZ)
        val radioSortZA = view.findViewById<RadioButton>(R.id.radioSortZA)

        closeButton.setOnClickListener { dismiss() }

        val args = arguments
        if (args != null) {
            activeCheckBox.isChecked = args.getBoolean("active", false)
            inactiveCheckBox.isChecked = args.getBoolean("inactive", false)
            remainingQuantityCheckBox.isChecked = args.getBoolean("remainingQuantity", false)

            // Retrieve the last selected sorting order and set the corresponding radio button as checked.
            currentSortOrder = args.getSerializable("sortOrder") as SortOrderEnum
            when (currentSortOrder) {
                SortOrderEnum.ASCENDING -> radioSortAZ.isChecked = true
                SortOrderEnum.DESCENDING -> radioSortZA.isChecked = true
                else -> {}
            }
        }

        applyButton.setOnClickListener {
            val active = activeCheckBox.isChecked
            val inactive = inactiveCheckBox.isChecked
            val remainingQuantity = remainingQuantityCheckBox.isChecked

            // Determine the selected sorting option
            val sortOrder = when (sortRadioGroup.checkedRadioButtonId) {
                R.id.radioSortAZ -> SortOrderEnum.ASCENDING
                R.id.radioSortZA -> SortOrderEnum.DESCENDING
                else -> SortOrderEnum.ASCENDING // Default to ascending if none is selected
            }

            CoroutineScope(Dispatchers.Main).launch {
                onFilterSortAppliedListener?.onFilterSortApplied(active, inactive, remainingQuantity, sortOrder)
            }
            dismiss()
        }

        resetButton.setOnClickListener {
            // Reset the checkboxes
            activeCheckBox.isChecked = true
            inactiveCheckBox.isChecked = true
            remainingQuantityCheckBox.isChecked = false
            val active = activeCheckBox.isChecked
            val inactive = inactiveCheckBox.isChecked
            val remainingQuantity = remainingQuantityCheckBox.isChecked

            // Reset the sort order to ascending A to Z
            radioSortAZ.isChecked = true
            val sortOrder = SortOrderEnum.ASCENDING

//            CoroutineScope(Dispatchers.Main).launch {
//                onFilterSortAppliedListener?.onFilterSortApplied(
//                    active,
//                    inactive,
//                    remainingQuantity,
//                    sortOrder
//                )
//            }
        }

        return view
    }

    companion object {
        fun newInstance(consumableFilterState: ConsumableFilterSortState, sortOrder: SortOrderEnum): FilterSortConsumableDialog {
            val fragment = FilterSortConsumableDialog()
            val args = Bundle()
            args.putBoolean("active", consumableFilterState.active)
            args.putBoolean("inactive", consumableFilterState.inactive)
            args.putBoolean("remainingQuantity", consumableFilterState.remainingQuantity)
            args.putSerializable("sortOrder", sortOrder)
            fragment.arguments = args
            return fragment
        }
    }

}
