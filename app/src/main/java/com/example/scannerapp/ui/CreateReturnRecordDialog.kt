package com.example.scannerapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.example.scannerapp.viewmodels.RecordViewModel
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class CreateReturnRecordDialog : DialogFragment(), CoroutineScope {
    private val job = Job()
    private var selectedBatchId: Int = -1
    private var hasValidScannedBatchNumber: Boolean = false
    private lateinit var userViewModel: UserViewModel
    private lateinit var batchDetailsViewModel: BatchDetailsViewModel
    private lateinit var consumableViewModel: ConsumableViewModel
    private lateinit var recordViewModel: RecordViewModel
    private lateinit var consumableFromBarcode: TextView
    private val activityScope = CoroutineScope(Dispatchers.Main)

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
        val batchNumberInputLayout = view.findViewById<TextInputLayout>(R.id.textInputLayoutBatchNumber)
        consumableFromBarcode =
            view.findViewById<TextInputEditText>(R.id.textInputEditTextConsumableTitleFromBarcodeData)
        val consumableFromBarcodeLayout =
            view.findViewById<TextInputLayout>(R.id.textInputLayoutConsumableTitleFromBarcodeData)
        val searchableSpinnerGuide = view.findViewById<TextView>(R.id.searchableSpinnerGuide)

        // Consumable Spinner
        val searchableSpinnerConsumable =
            view.findViewById<SearchableSpinner>(R.id.searchableSpinnerConsumable)
        searchableSpinnerConsumable.setTitle("Select Consumable");
        var consumableNames: List<String> = emptyList()
        var selectedConsumableId: Int = -1

        // Batch Spinner
        val searchableSpinnerBatch = view.findViewById<SearchableSpinner>(R.id.searchableSpinnerBatch)
        searchableSpinnerConsumable.setTitle("Select Consumable");

        // Retrieve the scanned data from the arguments (from barcode)
        val scannedData = arguments?.getString("scannedData")

        if (!scannedData.isNullOrBlank()) { // Barcode scanner detected barcode number
            processIfBatchNumberExists(scannedData)
            makeBatchConsumableSpinnersInvisible(
                searchableSpinnerBatch,
                searchableSpinnerConsumable,
                searchableSpinnerGuide
            )
            // Populate the batchNumberInput with the scanned data (from barcode)
            batchNumberInput.setText(scannedData)
            updateBatchDetailIdByBatchNumber(scannedData)
        } else {
            makeBatchConsumableTextViewsInvisible(batchNumberInputLayout, consumableFromBarcodeLayout)
        }

        recordViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        })

        recordViewModel.successLiveData.observe(viewLifecycleOwner, Observer { recordId ->
            Toast.makeText(requireContext(), "Record created successfully! $recordId", Toast.LENGTH_SHORT)
                .show()
            dismiss() // Only dismiss CreateRecordDialog here
        })

        // Searchable Spinner: Fetch the list of consumables from the ViewModel
        consumableViewModel.allActiveConsumables.observe(viewLifecycleOwner) { consumables ->
            // Update the consumableNames list when data is available
            consumableNames = consumables.map { it.getConsumableTitle() }
            // Create an ArrayAdapter and set it to the SearchableSpinner
            val consumableAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                consumableNames
            )
            searchableSpinnerConsumable.adapter = consumableAdapter

            searchableSpinnerConsumable.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Get the selected consumable item
                        val selectedConsumableName = consumableNames[position]

                        // Find the corresponding Consumable object based on the name
                        val selectedConsumable =
                            consumables.find { it.getConsumableTitle() == selectedConsumableName }

                        if (selectedConsumable != null) {
                            selectedConsumableId = selectedConsumable.consumableId

                            // Show the SearchableSpinner for Batch
                            showHideSpinnerBatch(
                                searchableSpinnerBatch = searchableSpinnerBatch,
                                selectedConsumableId = selectedConsumableId
                            )
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        // do nothing
                    }
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
            val userAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, userNames)
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
                titleView.text = "Add Take Out Record"
            } else {
                titleView.text = "Add Return Record"
            }
        }

        saveButton.setOnClickListener {
            // Get the current date
            val currentDate = LocalDate.now()
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val createDate = currentDate.format(dateFormatter)

            val quantityValue = quantityInput.text.toString().trim()
            val remarksValue = remarksInput.text.toString().trim() ?: ""
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

                batchId == -1 -> Toast.makeText(
                    requireContext(),
                    "Please select a Batch.",
                    Toast.LENGTH_SHORT
                ).show()

                userId == -1 -> Toast.makeText(
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

                    try {
                        val addSuccess = recordViewModel.addRecord(newRecord)
                    } catch (e: Exception) {
                        // a just-in-case Exception catch here
                    }
                }
            }
        }
    }

    private fun updateSearchableSpinnerSelection(
        searchableSpinnerConsumable: SearchableSpinner,
        selectedConsumableId: Int
    ) {
        consumableViewModel.allConsumables.observe(viewLifecycleOwner) { consumables ->
            val consumableAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                consumables.map { it.getConsumableTitle() })
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
            batchDetailsViewModel.allActiveBatchDetails.observe(viewLifecycleOwner) { batches ->

                // Sort batches by expiry date in ascending order (earliest first) and filter out batches with batchRemainingQuantity = 0
                val sortedBatches =
                    batches.filter { it.consumableId == selectedConsumableId && !isExpired(it.expiryDate) && it.batchRemainingQuantity > 0 }
                        .sortedBy { LocalDate.parse(it.expiryDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }


                // Extract batch numbers from the sorted list
                batchNumbers = sortedBatches.map { "${it.batchNumber} | Expiry Date: ${it.expiryDate}" }

                // Create an ArrayAdapter and set it to the SearchableSpinner
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    batchNumbers
                )
                searchableSpinnerBatch.adapter = adapter

                searchableSpinnerBatch.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            // Get the selected batch item
                            val selectedBatchNumber = sortedBatches[position].batchNumber

                            // Find the corresponding Batch object based on the name
                            val selectedBatch = batches.find { it.batchNumber == selectedBatchNumber }

                            if (selectedBatch != null) {

                                activityScope.launch {
                                    // Call the method here to check for earlier expiry batches.
                                    val shouldProceed = checkForEarlierExpiryBatches(selectedBatchNumber)
                                    if (shouldProceed) {
                                        selectedBatchId = selectedBatch.batchId
                                    } else {
                                        searchableSpinnerBatch.setSelection(-1)
                                        (searchableSpinnerBatch.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                                    }
                                }
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

    private suspend fun checkForEarlierExpiryBatches(enteredBatchNumber: String): Boolean {
        val enteredBatch = withContext(Dispatchers.IO) {
            batchDetailsViewModel.getBatchDetailsLiveDataByBatchNumber(enteredBatchNumber)
        }

        val earlierBatches = withContext(Dispatchers.IO) {
            val convertedDate = convertToSqlDateFormat(enteredBatch.expiryDate)
            batchDetailsViewModel.getBatchesWithEarlierExpiryDates(
                convertedDate,
                enteredBatch.consumableId
            ) // assuming expiryDate is a property of scannedBatch
        }

        // Logging the content of earlierBatches
        Log.d("EarlierBatches", "Content: $earlierBatches")

        if (earlierBatches.isNotEmpty()) {
            // Sort the list by expiry date
            val sortedEarlierBatches = earlierBatches.sortedBy { it.expiryDate }
            val firstBatch = sortedEarlierBatches.first()

            // Show dialog or toast to inform user
            withContext(Dispatchers.Main) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Attention")
                    .setMessage("Please take out batch ${firstBatch.batchNumber} with expiry date ${firstBatch.expiryDate} first.")
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
            return false // Don't proceed
        }
        return true // Proceed
    }

    fun convertToSqlDateFormat(inputDate: String): String {
        // Input format: dd/MM/yyyy
        // Output format: yyyy-MM-dd
        val parts = inputDate.split("/")
        return "${parts[2]}-${parts[1]}-${parts[0]}"
    }

    private fun isExpired(expiryDate: String?): Boolean {
        // Parse the expiry date string to a LocalDate object
        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val expiryLocalDate = LocalDate.parse(expiryDate, dateFormat)

        // Check if the expiry date is before the current date
        return expiryLocalDate.isBefore(LocalDate.now())
    }

    private fun processIfBatchNumberExists(batchDetailsBatchNumber: String) {
        var exists: Boolean
        activityScope.launch {
            val consumableFromBarcodeTitle = withContext(Dispatchers.IO) {
                exists = batchDetailsViewModel.checkIfBatchNumberExists(batchDetailsBatchNumber)
            }
            if (exists) {
                updateUIWithConsumableData(batchDetailsBatchNumber)
            }
        }
    }

    private fun makeBatchConsumableTextViewsInvisible(
        barcodeText: TextInputLayout,
        consumableText: TextInputLayout
    ) {
        // Make BatchNumber Text invisible (only visible if prefilled from barcode data)
        barcodeText.visibility = TextView.INVISIBLE
        val barcodeTextLayoutParams = barcodeText.layoutParams as ViewGroup.LayoutParams
        barcodeTextLayoutParams.height = 0
        val barcodeTextLayoutMarginParams =
            (barcodeText.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, 0, 0, 0)
            }
        barcodeText.layoutParams = barcodeTextLayoutParams

        // Make Consumable Text invisible (only visible if prefilled from barcode data)
        consumableText.visibility = TextView.INVISIBLE
        val consumableTextLayoutParams = consumableText.layoutParams as ViewGroup.LayoutParams
        consumableTextLayoutParams.height = 0
        val consumableTextLayoutMarginParams =
            (consumableText.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, 0, 0, 0)
            }
        barcodeText.layoutParams = consumableTextLayoutParams
    }

    private fun makeBatchConsumableSpinnersInvisible(
        searchableSpinnerBatch: SearchableSpinner,
        searchableSpinnerConsumable: SearchableSpinner,
        searchableSpinnerGuide: TextView
    ) {
        // Make Batch spinner invisible
        searchableSpinnerBatch.visibility = SearchableSpinner.INVISIBLE
        val batchSpinnerLayoutParams = searchableSpinnerBatch.layoutParams as ViewGroup.LayoutParams
        batchSpinnerLayoutParams.height = 0
        val searchableSpinnerBatchLayoutMarginParams =
            (searchableSpinnerBatch.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, 0, 0, 0)
            }
        searchableSpinnerBatch.layoutParams = batchSpinnerLayoutParams

        // Make Batch spinner invisible
        searchableSpinnerConsumable.visibility = SearchableSpinner.INVISIBLE
        val consumableSpinnerLayoutParams =
            searchableSpinnerConsumable.layoutParams as ViewGroup.LayoutParams
        consumableSpinnerLayoutParams.height = 0
        val searchableSpinnerConsumableLayoutMarginParams =
            (searchableSpinnerConsumable.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, 0, 0, 0)
            }
        searchableSpinnerConsumable.layoutParams = consumableSpinnerLayoutParams

        searchableSpinnerGuide.visibility = TextView.INVISIBLE
        val searchableSpinnerGuideLayoutParams =
            searchableSpinnerGuide.layoutParams as ViewGroup.LayoutParams
        searchableSpinnerGuideLayoutParams.height = 0
        searchableSpinnerGuide.layoutParams = searchableSpinnerGuideLayoutParams
    }

    private fun updateUIWithConsumableData(batchDetailsBatchNumber: String) {
        activityScope.launch {
            val consumableFromBarcodeTitle = withContext(Dispatchers.IO) {
                batchDetailsViewModel.getBatchDetailConsumableNameByBatchNumber(batchDetailsBatchNumber)
            }
            consumableFromBarcode.text = consumableFromBarcodeTitle
        }
    }

    private fun updateBatchDetailIdByBatchNumber(batchDetailsBatchNumber: String) {
        activityScope.launch {
            val batchDetailId = withContext(Dispatchers.IO) {
                batchDetailsViewModel.getBatchIdByBatchNumber(batchDetailsBatchNumber)
            }
            selectedBatchId = batchDetailId
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Cancel all coroutines
    }
}
