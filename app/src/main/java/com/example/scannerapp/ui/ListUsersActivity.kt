package com.example.scannerapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.scannerapp.R
import com.example.scannerapp.adapters.UserListAdapter
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.scannerapp.viewmodels.UserViewModel

class ListUsersActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users)

        // initialise the ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val userListView = findViewById<ListView>(R.id.userlist)

        // Observe the LiveData and update the adapter when data changes
        userViewModel.allUsers.observe(this, Observer { users ->
            val adapter = UserListAdapter(this, users)
            userListView.adapter = adapter

            adapter.updateData(users)
        })
    }
}