package com.example.scannerapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatSpinner
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.adapters.ConsumableSearchAdapter
import com.example.scannerapp.ui.ui.ui.theme.ScannerAppTheme
import com.example.scannerapp.viewmodels.RecordViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class CreateRecordDialog : DialogFragment() {
    private lateinit var recordViewModel: RecordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set dialog style
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_create_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // initialise the ViewModel
        recordViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
        //consumableListView = findViewById<ListView>(R.id.consumableDropdownList)

//        // Initialize and set up views and listeners here
//        val closeButton = view.findViewById<Button>(R.id.closeButton)
//        val textInputEditTextItem = view.findViewById<TextInputEditText>(R.id.textInputEditTextItem)
//        val textInputEditTextBrand =
//            view.findViewById<TextInputEditText>(R.id.textInputEditTextBrand)
//        val textInputEditTextType = view.findViewById<TextInputEditText>(R.id.textInputEditTextType)
//        val textInputEditTextSize = view.findViewById<TextInputEditText>(R.id.textInputEditTextSize)
//        val textInputEditTextNameItemCode =
//            view.findViewById<TextInputEditText>(R.id.textInputEditTextNameItemCode)
//        val spinnerUOM = view.findViewById<AppCompatSpinner>(R.id.spinnerUOM)
//        val textInputEditTextNamePerUnitQuantity =
//            view.findViewById<TextInputEditText>(R.id.textInputEditTextNamePerUnitQuantity)
//        val textInputEditTextNameMinQuantity =
//            view.findViewById<TextInputEditText>(R.id.textInputEditTextNameMinQuantity)
//        val switchStatus = view.findViewById<MaterialSwitch>(R.id.switchStatus)
//        val saveButton = view.findViewById<MaterialButton>(R.id.buttonSave)
        val consumablesAutoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.consumablesAutoCompleteTextView) // Consumables search dropdown

//        // Close the dialog when the close button is clicked
//        closeButton.setOnClickListener {
//            dismiss()
//        }

//        // Retrieve the string array from resources
//        val uomValues = resources.getStringArray(R.array.uom_values)
//
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        val adapter =
//            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, uomValues)

        // Consumables search dropdown
        val dropdownListView = view.findViewById<ListView>(R.id.consumableDropdownList)
        val consumableSearchAdapter = ConsumableSearchAdapter(requireContext(), R.id.consumableDropdownList, emptyList())
        val dropdownContainer = view.findViewById<FrameLayout>(R.id.dropdownContainer)

        // Create the PopupWindow
        val popupWindow = PopupWindow(dropdownContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        dropdownListView.adapter = consumableSearchAdapter
        consumablesAutoCompleteTextView.setAdapter(consumableSearchAdapter)
        // Set an item click listener to handle selection

        consumablesAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedConsumable = consumableSearchAdapter.getItem(position)
            consumablesAutoCompleteTextView.setText(selectedConsumable.consumableName)
            // Handle the selected item as needed
        }

        // Create a popup window for the ScrollView
        val inflater
        val dropdownContainer = inflater.inflate(R.layout.consumables_search_dropdown_component, null)
        val scrollView = dropdownContainer.findViewById<ScrollView>(R.id.scrollView)
        val listView = scrollView.findViewById<ListView>(R.id.consumableDropdownList)
        // End of Consumable search dropdown


//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        // Set the adapter to the Spinner
//        spinnerUOM.adapter = adapter
//
//        var selectedUOM = ""
//        spinnerUOM.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>?,
//                selectedItemView: View?,
//                position: Int,
//                id: Long
//            ) {
//                // Handle item selection here
//                selectedUOM = uomValues[position]
//                Log.d("SpinnerSelection", "Selected UOM: $selectedUOM")
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                // Handle no selection
//                spinnerUOM.prompt = "Select a Unit of Measurement"
//            }
//        }

        // Handle save button click
//        saveButton.setOnClickListener {
//
//            val item = textInputEditTextItem.text.toString().trim()
//            val brand = textInputEditTextBrand.text.toString().trim()
//            val type = textInputEditTextType.text.toString().trim()
//            val size = textInputEditTextSize.text.toString().trim()
//            val itemCode = textInputEditTextNameItemCode.text.toString().trim()
//            val uom = selectedUOM.capitalize()
//            val perUnitQuantityValue = textInputEditTextNamePerUnitQuantity.text.toString().trim()
//            val minQuantityValue = textInputEditTextNameMinQuantity.text.toString().trim()
//            val switchStatus = switchStatus.isChecked // true for enabled, false for disabled

//            if (item.isEmpty() || brand.isEmpty() || itemCode.isEmpty() || uom.isEmpty()) {
//
//                // display an error message if compulsory fields are empty
//                Toast.makeText(
//                    requireContext(),
//                    "Please fill in all required fields.",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//            } else {
//
//                // Check if perUnitQuantityValue and minQuantityValue are valid integers
//                val perUnitQuantity: Int = try {
//                    perUnitQuantityValue.toInt()
//                } catch (e: NumberFormatException) {
//                    // Handle the case where perUnitQuantityValue is not a valid integer
//                    Toast.makeText(
//                        requireContext(),
//                        "Please enter a valid Per Unit Quantity.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    -1 // Set a default or error value
//                    return@setOnClickListener // Exit the function early
//                }
//
//                val minQuantity: Int = try {
//                    minQuantityValue.toInt()
//                } catch (e: NumberFormatException) {
//                    // Handle the case where minQuantityValue is not a valid integer
//                    Toast.makeText(
//                        requireContext(),
//                        "Please enter a valid Minimum Quantity in Stock.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    -1 // Set a default or error value
//                    return@setOnClickListener // Exit the function early
//                }
//
//                val status: Int = if (switchStatus) {
//                    1
//                } else {
//                    0
//                } // 1 for enabled, 0 for disabled
//
//                val newConsumable = Consumable(
//                    consumableId = 0,
//                    consumableName = item,
//                    consumableBrand = brand,
//                    consumableType = type,
//                    consumableSize = size,
//                    barcodeId = itemCode,
//                    unitOfMeasurement = UnitOfMeasurement.valueOf(uom),
//                    perUnitQuantity = perUnitQuantity,
//                    minimumQuantity = minQuantity,
//                    isActive = status
//                )
//
//                // use the function in ViewModel to add the user
//                //recordViewModel.addConsumable(newConsumable)
//
//                // display success message
//                Toast.makeText(
//                    requireContext(),
//                    "Consumable created successfully!",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                dismiss()
//            }
//        }
    }
}



@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    ScannerAppTheme {
        Greeting2("Android")
    }
}