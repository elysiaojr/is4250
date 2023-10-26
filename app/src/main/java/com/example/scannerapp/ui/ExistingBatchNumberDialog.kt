package com.example.scannerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.widget.Button
import android.widget.TextView
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.BatchDetails

class ExistingBatchDialogFragment(private var batchDetails: BatchDetails) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate your custom layout for the dialog
        return inflater.inflate(R.layout.dialog_existing_batch_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find and set the message and button click listeners
        val messageTextView = view.findViewById<TextView>(R.id.dialog_message)
        val okButton = view.findViewById<Button>(R.id.dialog_ok_button)
        val editBatchButton = view.findViewById<Button>(R.id.dialog_edit_batch_button)

        // Retrieve the scanned data from the arguments (from barcode)
        val scannedData = arguments?.getString("scannedData")
        val scannedTextMessage = "Scanned: $scannedData"
        messageTextView.text = scannedTextMessage

        editBatchButton.setOnClickListener {
            batchDetails?.let {
                // Pass the BatchDetails object to EditBatchDetailsDialog
                val dialogFragment = EditBatchDetailsDialog(it, this)
                dialogFragment.show(childFragmentManager, "EditBatchDetailsDialog")
            }
        }

        okButton.setOnClickListener {
            // Handle the positive button click
            // You can add any custom logic here
            dismiss() // Close the dialog
        }
    }
}
