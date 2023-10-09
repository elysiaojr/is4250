package com.example.scannerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.database.entities.UnitOfMeasurement
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText


class EditConsumableDialog(private var consumable: Consumable) : DialogFragment() {
    private lateinit var consumableViewModel: ConsumableViewModel
    var consumableUpdatedListener: OnConsumableUpdatedListener? = null

    private lateinit var closeButton: Button
    private lateinit var textInputEditTextItem: TextInputEditText
    private lateinit var textInputEditTextBrand: TextInputEditText
    private lateinit var textInputEditTextType: TextInputEditText
    private lateinit var textInputEditTextSize: TextInputEditText
    private lateinit var textInputEditTextNameItemCode: TextInputEditText
    private lateinit var spinnerUOM: AppCompatSpinner
    private lateinit var textInputEditTextNamePerUnitQuantity: TextInputEditText
    private lateinit var textInputEditTextNameMinQuantity: TextInputEditText
    private lateinit var switchStatus: MaterialSwitch
    private lateinit var saveButton: MaterialButton

    // for updating the ConsumableDetails activity
    interface OnConsumableUpdatedListener {
        fun onConsumableUpdated(consumable: Consumable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set dialog style
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load the consumable when the view is created
        // consumableViewModel.getConsumable(consumable.consumableId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // initialise the ViewModel
        consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)

        val view = inflater.inflate(R.layout.dialog_edit_consumable, container, false)

        // Initialize and set up views and listeners here
        closeButton = view.findViewById(R.id.closeButton)
        textInputEditTextItem = view.findViewById(R.id.textInputEditTextItem)
        textInputEditTextBrand = view.findViewById(R.id.textInputEditTextBrand)
        textInputEditTextType = view.findViewById(R.id.textInputEditTextType)
        textInputEditTextSize = view.findViewById(R.id.textInputEditTextSize)
        textInputEditTextNameItemCode = view.findViewById(R.id.textInputEditTextNameItemCode)
        spinnerUOM = view.findViewById(R.id.spinnerUOM)
        textInputEditTextNamePerUnitQuantity = view.findViewById(R.id.textInputEditTextNamePerUnitQuantity)
        textInputEditTextNameMinQuantity = view.findViewById(R.id.textInputEditTextNameMinQuantity)
        switchStatus = view.findViewById(R.id.switchStatus)
        saveButton = view.findViewById(R.id.buttonSave)

        // Close the dialog when the close button is clicked
        closeButton.setOnClickListener {
            dismiss()
        }

        var selectedUOM = ""

        consumableViewModel.getConsumableById(consumable.consumableId).observe(viewLifecycleOwner
        ) { consumable ->
            if (consumable != null) {
                // Populate dialog with consumable data
                textInputEditTextItem.setText(consumable.consumableName)
                textInputEditTextBrand.setText(consumable.consumableBrand)
                textInputEditTextType.setText(consumable.consumableType)
                textInputEditTextSize.setText(consumable.consumableSize)
                textInputEditTextNameItemCode.setText(consumable.barcodeId)
                textInputEditTextNamePerUnitQuantity.setText(consumable.perUnitQuantity.toString())
                textInputEditTextNameMinQuantity.setText(consumable.minimumQuantity.toString())
                switchStatus.isChecked =
                    consumable.isActive == 1 // Set the switch based on user status

                selectedUOM = consumable.unitOfMeasurement.toString()
            }


            // Retrieve the string array from resources
            val uomValues = resources.getStringArray(R.array.uom_values)

            // Create an ArrayAdapter using the string array and a default spinner layout
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uomValues)

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Set the adapter to the Spinner
            spinnerUOM.adapter = adapter

            // Find the index of the selected UOM in the uomValues array
            val selectedIndex = uomValues.indexOf(selectedUOM)

            // Set the spinner selection to the index
            spinnerUOM.setSelection(selectedIndex)

            spinnerUOM.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    // Handle item selection here
                    selectedUOM = uomValues[position]
                    Log.d("SpinnerSelection", "Selected UOM: $selectedUOM")
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // Handle no selection
                    spinnerUOM.prompt = "Select a Unit of Measurement"
                }
            }


            // Handle save button click
            saveButton.setOnClickListener {

                val item = textInputEditTextItem.text.toString().trim()
                val brand = textInputEditTextBrand.text.toString().trim()
                val type = textInputEditTextType.text.toString().trim()
                val size = textInputEditTextSize.text.toString().trim()
                val itemCode = textInputEditTextNameItemCode.text.toString().trim()
                val uom = selectedUOM.capitalize()
                val perUnitQuantityValue =
                    textInputEditTextNamePerUnitQuantity.text.toString().trim()
                val minQuantityValue = textInputEditTextNameMinQuantity.text.toString().trim()
                val switchStatus = switchStatus.isChecked // true for enabled, false for disabled

                if (item.isEmpty() || brand.isEmpty() || itemCode.isEmpty() || uom.isEmpty()) {

                    // display an error message if compulsory fields are empty
                    Toast.makeText(
                        requireContext(),
                        "Please fill in all required fields.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener // Exit the lambda early

                } else {

                    // Check if perUnitQuantityValue and minQuantityValue are valid integers
                    val perUnitQuantity: Int = try {
                        perUnitQuantityValue.toInt()
                    } catch (e: NumberFormatException) {
                        // Handle the case where perUnitQuantityValue is not a valid integer
                        Toast.makeText(
                            requireContext(),
                            "Please enter a valid Per Unit Quantity.",
                            Toast.LENGTH_SHORT
                        ).show()
                        -1 // Set a default or error value
                        return@setOnClickListener // Exit the function early
                    }

                    // Check if perUnitQuantity is zero or less and display an error message
                    if (perUnitQuantity <= 0) {
                        Toast.makeText(
                            requireContext(),
                            "Per Unit Quantity must be greater than zero.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener // Exit the lambda early
                    }

                    val minQuantity: Int = try {
                        minQuantityValue.toInt()
                    } catch (e: NumberFormatException) {
                        // Handle the case where minQuantityValue is not a valid integer
                        Toast.makeText(
                            requireContext(),
                            "Please enter a valid Minimum Quantity in Stock.",
                            Toast.LENGTH_SHORT
                        ).show()
                        -1 // Set a default or error value
                        return@setOnClickListener // Exit the function early
                    }

                    // Check if minQuantity is zero or less and display an error message
                    if (minQuantity <= 0) {
                        Toast.makeText(
                            requireContext(),
                            "Minimum Quantity in Stock must be greater than zero.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener // Exit the lambda early
                    }

                    val status: Int = if (switchStatus) {
                        1
                    } else {
                        0
                    } // 1 for enabled, 0 for disabled

                    val updatedConsumable = Consumable(
                        consumableId = 0,
                        consumableName = item,
                        consumableBrand = brand,
                        consumableType = type,
                        consumableSize = size,
                        barcodeId = itemCode,
                        unitOfMeasurement = UnitOfMeasurement.valueOf(uom),
                        perUnitQuantity = perUnitQuantity,
                        minimumQuantity = minQuantity,
                        isActive = status
                    )

                    // use update method in viewModel to update the consumable
                    consumable?.let {
                        // If consumable is not null, it means we are editing an existing consumable
                        updatedConsumable.consumableId =
                            it.consumableId // Set the consumable ID to the existing consumable's ID

                        // Update the selectedConsumable LiveData with the updated consumable
                        consumableViewModel.selectedConsumable.value = updatedConsumable

                        consumableViewModel.updateConsumable(updatedConsumable)

                        // Notify the ConsumableDetails activity with the updated consumable
                        consumableUpdatedListener?.onConsumableUpdated(updatedConsumable)

                        dismiss()
                    }

                    // display success message
                    Toast.makeText(
                        requireContext(),
                        "Consumable updated successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener // Exit the lambda early

                }
            }
        }

        return view

    }
}


