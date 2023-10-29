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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
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

class CreateBatchDetailsDialog : DialogFragment(), CoroutineScope {
  private val job = Job()
  private var searchedConsumables: List<Consumable> = listOf()
  private lateinit var batchDetailsViewModel: BatchDetailsViewModel
  private lateinit var consumableViewModel: ConsumableViewModel
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
    return inflater.inflate(R.layout.dialog_create_batch_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
    consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)

    val closeButton = view.findViewById<Button>(R.id.closeButton)
    val batchNumberInput = view.findViewById<TextInputEditText>(R.id.textInputEditTextBatchNumber)
    val textViewExpiryDate = view.findViewById<TextView>(R.id.textViewExpiryDate)

    // Retrieve the scanned data from the arguments (from barcode)
    val scannedData = arguments?.getString("scannedData")

    // Populate the batchNumberInput with the scanned data (from barcode)
    batchNumberInput.setText(scannedData)

    batchDetailsViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { errorMessage ->
      Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    })

    batchDetailsViewModel.successLiveData.observe(viewLifecycleOwner, Observer {
      dismiss() // Only dismiss CreateBatchDetailsDialog here
    })

    batchDetailsViewModel.allBatchDetails.observe(viewLifecycleOwner, Observer { batchDetails ->
      // You need to get the batch number inside this observer to avoid reference issues
      val batchNumber = batchNumberInput.text.toString().trim()

      if (batchDetails.any { it.batchNumber == batchNumber }) {
        Toast.makeText(requireContext(), "Batch created successfully!", Toast.LENGTH_SHORT)
          .show()
        dismiss()
      }
    })

    val months = arrayOf(
      "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"
    )

    textViewExpiryDate.setOnClickListener {
      // 1. Generate the year and month lists
      val currentYear = LocalDate.now().year
      val currentMonth = LocalDate.now().monthValue
      val years = (currentYear..currentYear + 20).map { it.toString() }.toTypedArray()

      // 2. Create and show the custom dialog with two Spinners
      val dialogView =
        LayoutInflater.from(requireContext()).inflate(R.layout.month_year_picker_layout, null)
      val monthSpinner: Spinner = dialogView.findViewById(R.id.monthSpinner)
      val yearSpinner: Spinner = dialogView.findViewById(R.id.yearSpinner)

      yearSpinner.adapter =
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
      yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          val selectedYear = years[position].toInt()
          val displayMonths = if (selectedYear == currentYear) {
            months.sliceArray(currentMonth - 1 until months.size)
          } else {
            months
          }
          monthSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, displayMonths)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
          // No action needed
        }
      }

      val builder = AlertDialog.Builder(requireContext())
      builder.setTitle("Choose month and year")
      builder.setView(dialogView)
      builder.setPositiveButton("OK") { _, _ ->
        val selectedYear = yearSpinner.selectedItem.toString().toInt()
        val selectedMonth = monthSpinner.selectedItem.toString().let { selectedMonthName ->
          months.indexOf(selectedMonthName) + 1
        }

        // Show the date picker
        val datePickerDialog = DatePickerDialog(
          requireContext(),
          { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            textViewExpiryDate.text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
          },
          selectedYear,
          selectedMonth - 1,
          1
        )

        val datePicker = datePickerDialog.datePicker
        datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
      }
      builder.setNegativeButton("Cancel", null)
      builder.show()
    }


    val receivedQuantityInput =
      view.findViewById<TextInputEditText>(R.id.textInputEditTextReceivedQuantity)
    val switchStatus = view.findViewById<MaterialSwitch>(R.id.switchStatus)

    val searchableSpinnerConsumable =
      view.findViewById<SearchableSpinner>(R.id.searchableSpinnerConsumable)
    searchableSpinnerConsumable.setTitle("Select Consumable");
    var consumableNames: List<String> = emptyList()
    var selectedConsumableId: Int = -1

    // Fetch the list of consumables from the ViewModel
    consumableViewModel.allActiveConsumables.observe(viewLifecycleOwner) { consumables ->
      // Update the consumableNames list when data is available
      consumableNames =
        consumables.map { it.consumableName + ", " + it.consumableBrand + ", " + it.consumableType + ", " + it.consumableSize }

      // Create an ArrayAdapter and set it to the SearchableSpinner
      val adapter = ArrayAdapter(
        requireContext(),
        android.R.layout.simple_spinner_dropdown_item,
        consumableNames
      )
      searchableSpinnerConsumable.adapter = adapter

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
              consumables.find { it.consumableName + ", " + it.consumableBrand + ", " + it.consumableType + ", " + it.consumableSize == selectedConsumableName }

            if (selectedConsumable != null) {
              selectedConsumableId = selectedConsumable.consumableId
            }
          }

          override fun onNothingSelected(p0: AdapterView<*>?) {
            // do nothing
          }
        }
    }

    val saveButton = view.findViewById<MaterialButton>(R.id.buttonSave)

    closeButton.setOnClickListener { dismiss() }

    saveButton.setOnClickListener {
      val batchNumber = batchNumberInput.text.toString().trim()

      // Get the current date
      val currentDate = LocalDate.now()
      val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      val createDate = currentDate.format(dateFormatter)

      val expiryDate = textViewExpiryDate.text.toString().trim()
      val receivedQuantityValue = receivedQuantityInput.text.toString().trim()
      val isActive = if (switchStatus.isChecked) 1 else 0
      val consumableId = selectedConsumableId

      // Add validations here
      val expiryLocalDate =
        if (expiryDate.isNotEmpty()) LocalDate.parse(expiryDate, dateFormatter) else null

      when {

        batchNumber.isEmpty() -> Toast.makeText(
          requireContext(),
          "Batch Number is required.",
          Toast.LENGTH_SHORT
        ).show()

        batchNumber.length < 5 -> Toast.makeText(
          requireContext(),
          "Batch Number should be at least 5 characters long.",
          Toast.LENGTH_SHORT
        ).show()

        expiryDate.isEmpty() -> Toast.makeText(
          requireContext(),
          "Expiry Date is required.",
          Toast.LENGTH_SHORT
        ).show()

        expiryLocalDate != null && expiryLocalDate.isBefore(currentDate) -> Toast.makeText(
          requireContext(),
          "Expiry Date must be in the future.",
          Toast.LENGTH_SHORT
        ).show()

        receivedQuantityValue.isEmpty() -> Toast.makeText(
          requireContext(),
          "Quantity Received is required.",
          Toast.LENGTH_SHORT
        ).show()

        (receivedQuantityValue.toIntOrNull() ?: 0) <= 0 -> Toast.makeText(
          requireContext(),
          "Quantity Received should be a positive value.",
          Toast.LENGTH_SHORT
        ).show()

        consumableId == -1 -> Toast.makeText(
          requireContext(),
          "Please select a Consumable.",
          Toast.LENGTH_SHORT
        ).show()

        else -> {
          val receivedQuantity = receivedQuantityValue.toInt()

          // Process Strings
          val sanitisedBatchNumber = batchNumber.replace(Regex("\\n+"), ", ").trim()

          val newBatchDetail = BatchDetails(
            batchId = 0,
            batchNumber = sanitisedBatchNumber,
            createDate = createDate,
            expiryDate = expiryDate,
            batchReceivedQuantity = receivedQuantity,
            batchRemainingQuantity = receivedQuantity,
            isActive = isActive,
            consumableId = consumableId
          )

          batchDetailsViewModel.addBatchDetails(newBatchDetail)

        }
      }
    }

  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel() // Cancel all coroutines
  }
}
