package com.example.scannerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.viewmodels.BatchDetailsViewModel
import com.example.scannerapp.viewmodels.RecordViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.DialogFragment
//import com.example.scannerapp.R
//import com.example.scannerapp.database.entities.Record
//import com.example.scannerapp.viewmodels.BatchDetailsViewModel
//import com.example.scannerapp.viewmodels.RecordViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlin.coroutines.CoroutineContext
//
//class EditRecordsDialog(private var record: Record) :
//    DialogFragment(), CoroutineScope {
//    private val job = Job()
//    private lateinit var batchDetailsViewModel: BatchDetailsViewModel
//    private lateinit var recordViewModel: RecordViewModel
//    var recordUpdatedListener: onRecordsUpdatedListener? = null
//
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main + job
//
//    interface onRecordsUpdatedListener {
//        fun onRecordUpdated(record: Record)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Set dialog style
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
//    }
//
//    override fun OnCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.dialog_edit_record, container, false)
//    }
//}
//class EditRecordsDialog(private var record: Record) :
//    DialogFragment(), CoroutineScope {
//    private val job = Job()
//    private lateinit var batchDetailsViewModel: BatchDetailsViewModel
//    private lateinit var recordViewModel: RecordViewModel
//    var recordUpdatedListener: onRecordsUpdatedListener? = null
//
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main + job
//
//    interface onRecordsUpdatedListener {
//        fun onRecordUpdated(record: Record)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Set dialog style
//        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
//    }
//
//    override fun OnCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.dialog_edit_record, container, false)
//    }
//}