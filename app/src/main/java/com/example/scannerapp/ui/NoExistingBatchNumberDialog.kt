package com.example.scannerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.scannerapp.R

class NoExistingBatchDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate your custom layout for the dialog
        return inflater.inflate(R.layout.dialog_no_existing_batch_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Find and set the message and button click listeners
        val messageTextView = view.findViewById<TextView>(R.id.dialog_message)
        val okButton = view.findViewById<Button>(R.id.dialog_ok_button)
        val createNewBatchButton = view.findViewById<Button>(R.id.dialog_create_new_batch_button)

        // Retrieve the scanned data from the arguments (from barcode)
        val scannedData = arguments?.getString("scannedData")
        val scannedTextMessage = "Scanned: $scannedData"
        messageTextView.text = scannedTextMessage

        createNewBatchButton.setOnClickListener {
            val dialogFragment = CreateBatchDetailsDialog()
            val bundle = Bundle()
            bundle.putString("scannedData", scannedData)
            dialogFragment.arguments = bundle
            dialogFragment.show(childFragmentManager, "CreateRecordDialog")
        }

        okButton.setOnClickListener {
            // Handle the positive button click
            // You can add any custom logic here
            dismiss() // Close the dialog
        }
    }
}
