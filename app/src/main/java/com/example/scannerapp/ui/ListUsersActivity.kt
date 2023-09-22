package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.scannerapp.R
import com.example.scannerapp.adapters.UserListAdapter
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class ListUsersActivity : BaseActivity(R.layout.activity_list_users) {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    // for navigation bar
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ListUsersActivity::class.java)
        }
    }

    // for navigation bar
    override fun setActiveNavigationItem() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.item_settings
    }

}