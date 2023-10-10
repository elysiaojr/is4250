package com.example.scannerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.switchmaterial.SwitchMaterial

class CreateBatchDetailsDialog : DialogFragment() {

  private lateinit var batchDetailsViewModel: BatchDetailsViewModel

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

    val closeButton = view.findViewById<Button>(R.id.closeButton)
    val batchNumberInput = view.findViewById<TextInputEditText>(R.id.textInputEditTextBatchNumber)
    val createDateInput = view.findViewById<TextInputEditText>(R.id.textInputEditTextCreateDate)
    val expiryDateInput = view.findViewById<TextInputEditText>(R.id.textInputEditTextExpiryDate)
    val receivedQuantityInput =
      view.findViewById<TextInputEditText>(R.id.textInputEditTextReceivedQuantity)
    val remainingQuantityInput =
      view.findViewById<TextInputEditText>(R.id.textInputEditTextRemainingQuantity)
    val switchStatus = view.findViewById<SwitchMaterial>(R.id.switchStatus)
    val consumableIdInput = view.findViewById<TextInputEditText>(R.id.textInputEditTextConsumableId)
    val saveButton = view.findViewById<MaterialButton>(R.id.buttonSave)

    closeButton.setOnClickListener { dismiss() }

    saveButton.setOnClickListener {
      val batchNumber = batchNumberInput.text.toString().trim()
      val createDate = createDateInput.text.toString().trim()
      val expiryDate = expiryDateInput.text.toString().trim()
      val receivedQuantityValue = receivedQuantityInput.text.toString().trim()
      val remainingQuantityValue = remainingQuantityInput.text.toString().trim()
      val isActive = if (switchStatus.isChecked) 1 else 0
      val consumableIdValue = consumableIdInput.text.toString().trim()

      if (batchNumber.isEmpty() || createDate.isEmpty() || expiryDate.isEmpty() || receivedQuantityValue.isEmpty() || remainingQuantityValue.isEmpty() || consumableIdValue.isEmpty()) {
        Toast.makeText(requireContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT)
          .show()
      } else {
        val receivedQuantity = receivedQuantityValue.toInt()
        val remainingQuantity = remainingQuantityValue.toInt()
        val consumableId = consumableIdValue.toInt()

        val newBatchDetail = BatchDetails(
          batchNumber = batchNumber,
          createDate = createDate,
          expiryDate = expiryDate,
          batchReceivedQuantity = receivedQuantity,
          batchRemainingQuantity = remainingQuantity,
          isActive = isActive,
          consumableId = consumableId
        )

        batchDetailsViewModel.addBatchDetails(newBatchDetail)

        Toast.makeText(requireContext(), "Batch Detail created successfully!", Toast.LENGTH_SHORT)
          .show()

        dismiss()
      }
    }
  }
}
