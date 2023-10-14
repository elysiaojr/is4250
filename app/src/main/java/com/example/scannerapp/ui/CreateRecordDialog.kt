package com.example.scannerapp.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.example.scannerapp.viewmodels.RecordViewModel
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.materialswitch.MaterialSwitch
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class CreateRecordDialog : DialogFragment(), CoroutineScope {
    private val job = Job()
    private var selectedBatchId: Int = -1
    private var hasValidScannedBatchNumber: Boolean = false
    private lateinit var userViewModel: UserViewModel
    private lateinit var batchDetailsViewModel: BatchDetailsViewModel
    private lateinit var consumableViewModel: ConsumableViewModel
    private lateinit var recordViewModel: RecordViewModel
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)
        batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
        recordViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val closeButton = view.findViewById<Button>(R.id.closeButton)
        val quantityInput =
            view.findViewById<TextInputEditText>(R.id.textInputEditTextQuantity)
        val remarksInput =
            view.findViewById<TextInputEditText>(R.id.textInputEditTextRemarks)
        val switchRecordType = view.findViewById<MaterialSwitch>(R.id.switchRecordType)
        val switchStatus = view.findViewById<MaterialSwitch>(R.id.switchStatus)
        val batchNumberInput = view.findViewById<TextInputEditText>(R.id.textInputEditTextBatchNumber)
        val consumableTitleInput = view.findViewById<TextInputEditText>(R.id.textInputEditTextConsumableTitleFromBarcodeData)

        // Consumable Spinner
        val searchableSpinnerConsumable = view.findViewById<SearchableSpinner>(R.id.searchableSpinnerConsumable)
        searchableSpinnerConsumable.setTitle("Select Consumable");
        var consumableNames: List<String> = emptyList()
        var selectedConsumableId: Int = -1

        // Batch Spinner
        val searchableSpinnerBatch = view.findViewById<SearchableSpinner>(R.id.searchableSpinnerBatch)
        searchableSpinnerConsumable.setTitle("Select Consumable");

        // Retrieve the scanned data from the arguments (from barcode)
        val scannedData = arguments?.getString("scannedData")
        // Populate the batchNumberInput with the scanned data (from barcode)
        batchNumberInput.setText(scannedData)
        // Barcode scanner detected barcode number

        if (scannedData?.length != 0) {
            searchableSpinnerBatch.visibility = SearchableSpinner.VISIBLE
            searchableSpinnerConsumable.visibility = SearchableSpinner.VISIBLE

            consumableViewModel.allConsumables.observe(viewLifecycleOwner) { consumables ->
                val selectedConsumable = consumables.find { it.consumableId == selectedConsumableId }
                consumableTitleInput.setText(selectedConsumable?.getConsumableTitle())

//                for (consumable in Consumable) {
//                    if (batchDetail.batchNumber == scannedData) {
//                        // Batch Number exists in the database
//                        selectedConsumableId = batchDetail.consumableId
//                        selectedBatchId = batchDetail.batchId
//                        hasValidScannedBatchNumber = true
//
//                        // Update the searchableSpinnerConsumable selection when the data is available
//                        updateSearchableSpinnerSelection(searchableSpinnerConsumable, selectedConsumableId)
//                        break;
//                    }
//                }
            }
//            if (hasValidScannedBatchNumber) {
//                // Prompt user to create new batch
//                // Searchable Spinner: Fetch the list of consumables from the ViewModel
//                consumableViewModel.allConsumables.observe(viewLifecycleOwner) { consumables ->
//                    val consumableAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, consumables)
//                    searchableSpinnerConsumable.adapter = consumableAdapter
//                    searchableSpinnerConsumable.setSelection(consumableAdapter.getPosition(consumable.consumableId == selectedConsumableId))
//                }
//            } else {
//
//            }
        }


        recordViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        })

        recordViewModel.allRecords.observe(viewLifecycleOwner, Observer { record ->
        })

        // Searchable Spinner: Fetch the list of consumables from the ViewModel
        consumableViewModel.allConsumables.observe(viewLifecycleOwner) { consumables ->
            // Update the consumableNames list when data is available
            consumableNames = consumables.map { it.getConsumableTitle() }
            // Create an ArrayAdapter and set it to the SearchableSpinner
            val consumableAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, consumableNames)
            searchableSpinnerConsumable.adapter = consumableAdapter

//            if (hasValidScannedBatchNumber) {
////                println("selectedConsumable: " + selectedConsumableId)
////                val selectedConsumableById = consumables.find {it.consumableId == selectedConsumableId }
////                println("selectedConsumable: by ID " + selectedConsumableById)
////                val selectedConsumableByIdTitle = selectedConsumableById?.getConsumableTitle()
////                println("selectedConsumable: by ID title" + selectedConsumableByIdTitle)
////                println("selectedConsumable: consumableAdapter.getPosition " + consumableAdapter.getPosition(selectedConsumableByIdTitle))
////                searchableSpinnerConsumable.setSelection(consumableAdapter.getPosition(selectedConsumableByIdTitle))
//            } else {
                searchableSpinnerConsumable.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // Get the selected consumable item
                        val selectedConsumableName = consumableNames[position]

                        // Find the corresponding Consumable object based on the name
                        val selectedConsumable = consumables.find { it.getConsumableTitle() == selectedConsumableName }

                        if (selectedConsumable != null) {
                            selectedConsumableId = selectedConsumable.consumableId

                            // Show the SearchableSpinner for Batch
                            showHideSpinnerBatch(searchableSpinnerBatch = searchableSpinnerBatch, selectedConsumableId = selectedConsumableId)
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        // do nothing
                    }
//                }
            }
        }

        // User Spinner
        val searchableSpinnerUser = view.findViewById<SearchableSpinner>(R.id.searchableSpinnerUser)
        searchableSpinnerUser.setTitle("Select User");
        var userNames: List<String> = emptyList()
        var selectedUserId: Int = -1

        // Searchable Spinner: Fetch the list of consumables from the ViewModel
        userViewModel.allUsers.observe(viewLifecycleOwner) { users ->

            // Update the consumableNames list when data is available
            userNames = users.map { it.name }

            // Create an ArrayAdapter and set it to the SearchableSpinner
            val userAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, userNames)
            searchableSpinnerUser.adapter = userAdapter

            searchableSpinnerUser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Get the selected consumable item
                    val selectedUserName = userNames[position]

                    // Find the corresponding Consumable object based on the name
                    val selectedUser = users.find { it.name == selectedUserName }

                    if (selectedUser != null) {
                        selectedUserId = selectedUser.userId
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // do nothing
                }
            }
        }

        val saveButton = view.findViewById<MaterialButton>(R.id.buttonSave)

        closeButton.setOnClickListener { dismiss() }

        val titleView = view.findViewById<TextView>(R.id.titleViewAddRecord)

        switchRecordType.setOnClickListener {
            if (switchRecordType.isChecked) {
                titleView.text = "Add Take-Out Record"
            } else {
                titleView.text = "Add Put-In Record"
            }
        }

        saveButton.setOnClickListener {
//            val batchNumber = batchNumberInput.text.toString().trim()

            // Get the current date
            val currentDate = LocalDate.now()
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val createDate = currentDate.format(dateFormatter)

            val quantityValue = quantityInput.text.toString().trim()
            val remarksValue = remarksInput.text.toString().trim()
            val isActive = if (switchStatus.isChecked) 1 else 0
            val isTakeOut = if (switchRecordType.isChecked) RecordType.TAKE_OUT else RecordType.PUT_IN
            val consumableId = selectedConsumableId
            val batchId = selectedBatchId
            val userId = selectedUserId

            when {
                quantityValue.isEmpty() -> Toast.makeText(
                    requireContext(),
                    "Quantity Received is required.",
                    Toast.LENGTH_SHORT
                ).show()

                (quantityValue.toIntOrNull() ?: 0) <= 0 -> Toast.makeText(
                    requireContext(),
                    "Quantity should be more than 0.",
                    Toast.LENGTH_SHORT
                ).show()

                batchId == -1 ->  Toast.makeText(
                    requireContext(),
                    "Please select a Batch.",
                    Toast.LENGTH_SHORT
                ).show()

                userId == -1 ->  Toast.makeText(
                    requireContext(),
                    "Please select a user.",
                    Toast.LENGTH_SHORT
                ).show()

                else -> {
                    val quantity = quantityValue.toInt()

                    val newRecord = Record(
                        recordId = 0,
                        recordDate = createDate,
                        recordQuantityChanged = quantity,
                        recordRemarks = remarksValue,
                        recordType = isTakeOut,
                        isActive = isActive,
                        batchId = batchId,
                        userId = userId
                    )

                    recordViewModel.addRecord(newRecord)


                    dismiss()
                }
            }
        }

    }
    private fun updateSearchableSpinnerSelection(searchableSpinnerConsumable: SearchableSpinner, selectedConsumableId: Int) {
        consumableViewModel.allConsumables.observe(viewLifecycleOwner) { consumables ->
            val consumableAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, consumables.map { it.getConsumableTitle() })
            searchableSpinnerConsumable.adapter = consumableAdapter

            val selectedConsumable = consumables.find { it.consumableId == selectedConsumableId }

            if (selectedConsumable != null) {
                val selectedConsumableTitle = selectedConsumable.getConsumableTitle()
                val position = consumableAdapter.getPosition(selectedConsumableTitle)

                // Set the selection of the searchableSpinnerConsumable
                searchableSpinnerConsumable.setSelection(position)
            }
        }
    }

    fun showHideSpinnerBatch(searchableSpinnerBatch: SearchableSpinner, selectedConsumableId: Int) {
        if (selectedConsumableId != -1) {
            var batchNumbers: List<String> = emptyList()

            // Searchable Spinner: Filer by SelectedConsumableId
            batchDetailsViewModel.allBatchDetails.observe(viewLifecycleOwner) { batches ->

                batchNumbers = batches
                    .filter { it.consumableId == selectedConsumableId }
                    .map { it.batchNumber }

                // Create an ArrayAdapter and set it to the SearchableSpinner
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, batchNumbers)
                searchableSpinnerBatch.adapter = adapter

                searchableSpinnerBatch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // Get the selected consumable item
                        val selectedBatchNumber = batchNumbers[position]

                        // Find the corresponding Consumable object based on the name
                        val selectedBatch = batches.find { it.batchNumber == selectedBatchNumber }

                        if (selectedBatch != null) {
                            selectedBatchId = selectedBatch.batchId
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        // do nothing
                    }
                }
            }

            // Make SearchView visible
            searchableSpinnerBatch.visibility = SearchableSpinner.VISIBLE
            // Restore the original height (or set a specific height if needed)
            val layoutParams = searchableSpinnerBatch.layoutParams as ViewGroup.LayoutParams
            layoutParams.height =
                116 // You can use specific height if needed
            searchableSpinnerBatch.layoutParams = layoutParams

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Cancel all coroutines
    }
}
