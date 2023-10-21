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
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.database.entities.UnitOfMeasurement
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class CreateConsumableDialog : DialogFragment() {
  private lateinit var consumableViewModel: ConsumableViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Set dialog style
    setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.dialog_create_consumable, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // initialise the ViewModel
    consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)

    // Initialize and set up views and listeners here
    val closeButton = view.findViewById<Button>(R.id.closeButton)
    val textInputEditTextItem = view.findViewById<TextInputEditText>(R.id.textInputEditTextItem)
    val textInputEditTextBrand = view.findViewById<TextInputEditText>(R.id.textInputEditTextBrand)
    val textInputEditTextType = view.findViewById<TextInputEditText>(R.id.textInputEditTextType)
    val textInputEditTextSize = view.findViewById<TextInputEditText>(R.id.textInputEditTextSize)
    val textInputEditTextNameItemCode =
      view.findViewById<TextInputEditText>(R.id.textInputEditTextNameItemCode)
    val spinnerUOM = view.findViewById<AppCompatSpinner>(R.id.spinnerUOM)
    val textInputEditTextNameMinQuantity =
      view.findViewById<TextInputEditText>(R.id.textInputEditTextNameMinQuantity)
    val switchStatus = view.findViewById<MaterialSwitch>(R.id.switchStatus)
    val saveButton = view.findViewById<MaterialButton>(R.id.buttonSave)

    // Close the dialog when the close button is clicked
    closeButton.setOnClickListener {
      dismiss()
    }

    // Retrieve the string array from resources
    val uomValues = resources.getStringArray(R.array.uom_values)

    // Create an ArrayAdapter using the string array and a default spinner layout
    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uomValues)

    // Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    // Set the adapter to the Spinner
    spinnerUOM.adapter = adapter

    var selectedUOM = ""
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
      val minQuantityValue = textInputEditTextNameMinQuantity.text.toString().trim()
      val switchStatus = switchStatus.isChecked // true for enabled, false for disabled

      if (item.isEmpty() || brand.isEmpty() || itemCode.isEmpty() || uom.isEmpty()) {

        // display an error message if compulsory fields are empty
        Toast.makeText(requireContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT)
          .show()

      } else {

        // Check if minQuantityValue are valid integers
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
          return@setOnClickListener // Exit the function early
        }

        val status: Int = if (switchStatus) {
          1
        } else {
          0
        } // 1 for enabled, 0 for disabled

        // Process Strings
        val sanitisedItem = item.replace(Regex("\\n+"), ", ").trim()
        val sanitisedBrand = brand.replace(Regex("\\n+"), ", ").trim()
        val sanitisedType = type.replace(Regex("\\n+"), ", ").trim()
        val sanitisedSize = size.replace(Regex("\\n+"), ", ").trim()
        val sanitisedItemCode = itemCode.replace(Regex("\\n+"), ", ").trim()

        val newConsumable = Consumable(
          consumableId = 0,
          consumableName = sanitisedItem,
          consumableBrand = sanitisedBrand,
          consumableType = sanitisedType,
          consumableSize = sanitisedSize,
          itemCode = sanitisedItemCode,
          unitOfMeasurement = UnitOfMeasurement.valueOf(uom),
          minimumQuantity = minQuantity,
          isActive = status
        )

        // use the function in ViewModel to add the user
        consumableViewModel.addConsumable(newConsumable)

        // display success message
        Toast.makeText(requireContext(), "Consumable created successfully!", Toast.LENGTH_SHORT)
          .show()

        dismiss()
      }
    }
  }
}

