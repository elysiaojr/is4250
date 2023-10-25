package com.example.scannerapp.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.ConsumableViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class EditBatchDetailsDialog(private var batchDetails: BatchDetails) :
  DialogFragment(), CoroutineScope {
  private val job = Job()
  private lateinit var consumableViewModel: ConsumableViewModel
  private lateinit var batchDetailsViewModel: BatchDetailsViewModel
  var batchDetailsUpdatedListener: OnBatchDetailsUpdatedListener? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

  // for updating the ConsumableDetails activity
  interface OnBatchDetailsUpdatedListener {
    fun onBatchDetailsUpdated(batchDetails: BatchDetails)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Set dialog style
    setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)

  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.dialog_edit_batch_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    batchDetailsViewModel = ViewModelProvider(this).get(BatchDetailsViewModel::class.java)
    consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)

    // Initialize and set up views and listeners here, and populate with existing data
    val closeButton = view.findViewById<Button>(R.id.closeButton)

    val batchNumberInput =
      view.findViewById<TextInputEditText>(R.id.textInputEditTextBatchNumber)
    batchNumberInput.setText(batchDetails.batchNumber)

    val textViewExpiryDate = view.findViewById<TextView>(R.id.textViewExpiryDate)
    textViewExpiryDate.text = batchDetails.expiryDate

    // Parse the existing date to extract year, month, and day
    val dateParts = batchDetails.expiryDate.split("/")
    val year = dateParts[2].toInt()
    val month = dateParts[1].toInt() - 1 // Month is zero-based in DatePicker
    val day = dateParts[0].toInt()

    batchDetailsViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { errorMessage ->
      Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    })

//        batchDetailsViewModel.allBatchDetails.observe(viewLifecycleOwner, Observer { batchDetails ->
//            // You need to get the batch number inside this observer to avoid reference issues
//            val batchNumber = batchNumberInput.text.toString().trim()
//
//            if (batchDetails.any { it.batchNumber == batchNumber }) {
//                Toast.makeText(
//                    requireContext(),
//                    "Batch Detail created successfully!",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//                dismiss()
//            }
//        })

    val months = arrayOf(
      "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"
    )
    val currentYear = LocalDate.now().year
    val numberOfYears = 50  // for example, you can adjust this as needed
    val years = Array(numberOfYears) { i -> "${i + currentYear}" }

    textViewExpiryDate.setOnClickListener {
      val dialogView =
        LayoutInflater.from(requireContext())
          .inflate(R.layout.month_year_picker_layout, null)
      val monthSpinner: Spinner = dialogView.findViewById(R.id.monthSpinner)
      val yearSpinner: Spinner = dialogView.findViewById(R.id.yearSpinner)

      monthSpinner.adapter =
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
      yearSpinner.adapter =
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)

      // Set the selections in the spinners based on the parsed values
      monthSpinner.setSelection(month)
      val selectedYear = years.indexOf(year.toString())
      yearSpinner.setSelection(selectedYear)

      val builder = AlertDialog.Builder(requireContext())
      builder.setTitle("Choose month and year")
      builder.setView(dialogView)
      builder.setPositiveButton("OK") { _, _ ->
        val selectedYear = yearSpinner.selectedItem.toString().toInt()
        val selectedMonth = months.indexOf(monthSpinner.selectedItem.toString())

        // Now show day picker
        val datePickerDialog = DatePickerDialog(
          requireContext(),
          { _, _, monthOfYear, dayOfMonth ->
            val selectedDate = LocalDate.of(selectedYear, monthOfYear + 1, dayOfMonth)
            textViewExpiryDate.text =
              selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
          },
          selectedYear,
          selectedMonth,
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
    receivedQuantityInput.setText(batchDetails.batchReceivedQuantity.toString())

    val switchStatus = view.findViewById<MaterialSwitch>(R.id.switchStatus)
    switchStatus.isChecked =
      batchDetails.isActive == 1 // Set the switch based on batchDetails status

    val searchableSpinnerConsumable =
      view.findViewById<SearchableSpinner>(R.id.searchableSpinnerConsumable)
    searchableSpinnerConsumable.setTitle("Select Consumable");

    var consumableNames: List<String> = emptyList()
    var selectedConsumableId = batchDetails.consumableId

    // Fetch the list of consumables from the ViewModel
    consumableViewModel.allConsumables.observe(viewLifecycleOwner) { consumables ->
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

      val selectedIndex = consumables.indexOfFirst { it.consumableId == selectedConsumableId }

      // Ensure that the selected index is valid
      if (selectedIndex != -1) {
        searchableSpinnerConsumable.setSelection(selectedIndex)
      }

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

      val currentDate = LocalDate.now()
      val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      val createDate = batchDetails.createDate.format(dateFormatter)

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
          val receivedQuantityUpdated = receivedQuantityValue.toInt()


          // Process Strings
          val sanitisedBatchNumber = batchNumber.replace(Regex("\\n+"), ", ").trim()

          activityScope.launch {
            val remainingQuantity =
              batchDetailsViewModel.getBatchDetailsLiveDataByBatchNumber(sanitisedBatchNumber).batchRemainingQuantity
            val receivedQuantityActual =
              batchDetailsViewModel.getBatchDetailsLiveDataByBatchNumber(sanitisedBatchNumber).batchReceivedQuantity

            val receivedQuantityDifference = receivedQuantityUpdated - receivedQuantityActual

            // Increase in Received Quantity
            if (receivedQuantityDifference >= 0) {
              val updatedBatchDetails = BatchDetails(
                batchId = 0,
                batchNumber = sanitisedBatchNumber,
                createDate = createDate,
                expiryDate = expiryDate,
                batchReceivedQuantity = receivedQuantityUpdated,
                batchRemainingQuantity = remainingQuantity + receivedQuantityDifference,
                isActive = isActive,
                consumableId = consumableId
              )

              // use update method in viewModel to update the batchDetails
              batchDetails?.let {
                // If batchDetails is not null, it means we are editing an existing batchDetails
                updatedBatchDetails.batchId =
                  it.batchId // Set the batch ID to the existing batch's ID

                // Update the selectedBatchDetails LiveData with the updated consumable
                batchDetailsViewModel.selectedBatchDetails.value = updatedBatchDetails

                batchDetailsViewModel.updateBatchDetails(updatedBatchDetails)

                // Notify the BatchDetailsActivity with the updated batchDetails
                batchDetailsUpdatedListener?.onBatchDetailsUpdated(updatedBatchDetails)

                dismiss()
              }

              // display success message
              Toast.makeText(
                requireContext(),
                "Batch Details updated successfully!",
                Toast.LENGTH_SHORT
              ).show()
              // Decrease in Received Quantity
            } else if (receivedQuantityDifference < 0 && Math.abs(receivedQuantityDifference) <= remainingQuantity) {

              val updatedBatchDetails = BatchDetails(
                batchId = 0,
                batchNumber = sanitisedBatchNumber,
                createDate = createDate,
                expiryDate = expiryDate,
                batchReceivedQuantity = receivedQuantityUpdated,
                batchRemainingQuantity = remainingQuantity - Math.abs(receivedQuantityDifference),
                isActive = isActive,
                consumableId = consumableId
              )

              // use update method in viewModel to update the batchDetails
              batchDetails?.let {
                // If batchDetails is not null, it means we are editing an existing batchDetails
                updatedBatchDetails.batchId =
                  it.batchId // Set the batch ID to the existing batch's ID

                // Update the selectedBatchDetails LiveData with the updated consumable
                batchDetailsViewModel.selectedBatchDetails.value = updatedBatchDetails

                batchDetailsViewModel.updateBatchDetails(updatedBatchDetails)

                // Notify the BatchDetailsActivity with the updated batchDetails
                batchDetailsUpdatedListener?.onBatchDetailsUpdated(updatedBatchDetails)

                dismiss()
              }

              // display success message
              Toast.makeText(
                requireContext(),
                "Batch Details updated successfully!",
                Toast.LENGTH_SHORT
              ).show()
            } else {
              Toast.makeText(
                requireContext(),
                "Updated batch received quantity will cause balance stock to be lower than 0. Please verify the number again.",
                Toast.LENGTH_SHORT
              ).show()
            }

          }

          return@setOnClickListener // Exit the lambda early

        }
      }
    }

  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel() // Cancel all coroutines
  }
}
