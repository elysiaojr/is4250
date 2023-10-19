package com.example.scannerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private var currentFilterStatus: Int = 1
    private var currentFilterQuantity: Int = 1


    interface OnFilterSortAppliedListener {
        suspend fun onFilterSortApplied(
            status: Int,
            quantity: Int,
            sortOrder: SortOrderEnum
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_filter_sort_consumables, container, false)
        val closeButton = view.findViewById<Button>(R.id.closeButton)

        val filterStatusRadioGroup = view.findViewById<RadioGroup>(R.id.filterStatusRadioGroup)
        val activeRadioButton = view.findViewById<RadioButton>(R.id.activeRadioButton)
        val inactiveRadioButton = view.findViewById<RadioButton>(R.id.inactiveRadioButton)
        val filterQuantityRadioGroup = view.findViewById<RadioGroup>(R.id.filterQuantityRadioGroup)
        val sufficientRadioButton = view.findViewById<RadioButton>(R.id.sufficientRadioButton)
        val shortageRadioButton = view.findViewById<RadioButton>(R.id.shortageRadioButton)

        val applyButton = view.findViewById<Button>(R.id.buttonApply)
        val resetButton = view.findViewById<Button>(R.id.buttonReset)
        val sortRadioGroup = view.findViewById<RadioGroup>(R.id.sortRadioGroup)
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

            currentFilterQuantity = args.getInt("quantity")
            when (currentFilterQuantity) {
                1 -> sufficientRadioButton.isChecked = true
                0 -> shortageRadioButton.isChecked = true
            }

            // Retrieve the last selected sorting order and set the corresponding radio button as checked.
            currentSortOrder = args.getSerializable("sortOrder") as SortOrderEnum
            when (currentSortOrder) {
                SortOrderEnum.ASCENDING -> radioSortAZ.isChecked = true
                SortOrderEnum.DESCENDING -> radioSortZA.isChecked = true
                else -> {}
            }
        }

        applyButton.setOnClickListener {
            val filterStatus = when (filterStatusRadioGroup.checkedRadioButtonId) {
                R.id.activeRadioButton -> 1
                R.id.inactiveRadioButton -> 0
                else -> {1}
            }

            val filterQuantity = when (filterQuantityRadioGroup.checkedRadioButtonId) {
                R.id.sufficientRadioButton -> 1
                R.id.shortageRadioButton -> 0
                else -> {1}
            }

            // Determine the selected sorting option
            val sortOrder = when (sortRadioGroup.checkedRadioButtonId) {
                R.id.radioSortAZ -> SortOrderEnum.ASCENDING
                R.id.radioSortZA -> SortOrderEnum.DESCENDING
                else -> SortOrderEnum.ASCENDING // Default to ascending if none is selected
            }

            CoroutineScope(Dispatchers.Main).launch {
                onFilterSortAppliedListener?.onFilterSortApplied(filterStatus, filterQuantity, sortOrder)
            }
            dismiss()
        }

        resetButton.setOnClickListener {
            // Reset the filters
            activeRadioButton.isChecked = true
            sufficientRadioButton.isChecked = true

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
            args.putInt("status", consumableFilterState.status)
            args.putInt("quantity", consumableFilterState.quantity)
            args.putSerializable("sortOrder", sortOrder)
            fragment.arguments = args
            return fragment
        }
    }

}
