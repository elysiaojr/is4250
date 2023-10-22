package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.scannerapp.R
import com.example.scannerapp.adapters.UserListAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class EditPinActivity : AppCompatActivity(R.layout.activity_edit_pin) {
    private lateinit var userViewModel: UserViewModel
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backButton = findViewById<ImageView>(R.id.back_icon)

        backButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

//        setContentView(R.layout.activity_list_users)

//        // initialise the ViewModel
//        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
//        val userListView = findViewById<ListView>(R.id.userlist)
//        backButton = findViewById<ImageView>(R.id.back_icon)
//
//        backButton.setOnClickListener {
//            startActivity(Intent(this, MainLandingActivity::class.java))
//        }
//
//        // Observe the LiveData and update the adapter when data changes
//        userViewModel.allUsers.observe(this, Observer { users ->
//            val adapter = UserListAdapter(this, users)
//            userListView.adapter = adapter
//
//            adapter.updateData(users)
//        })
    }
}