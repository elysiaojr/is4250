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

//        // Define Users
//        val arrayAdapter: ArrayAdapter<*>
////        val users = arrayOf(
////            "Virat Kohli", "Rohit Sharma", "Steve Smith",
////            "Kane Williamson", "Ross Taylor"
////        )
//        val users = userViewModel.allUsers
//
//        // access the listView from xml file
//        var mListView = findViewById<ListView>(R.id.userlist)
//
//        arrayAdapter = ArrayAdapter(this,
//            android.R.layout.simple_list_item_1, users)
//        mListView.adapter = arrayAdapter


        // NEW
//        // Access the listView from XML file
//        val mListView = findViewById<ListView>(R.id.userlist)
//
//        // Create an empty adapter
//        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
//
//        // Set the adapter to the ListView
//        mListView.adapter = arrayAdapter
//
//        // Observe the LiveData and update the adapter when data changes
//        userViewModel.allUsers.observe(this, Observer { users ->
//            val userNames = users.map { it.name } // Assuming 'name' is the property you want to display
//            arrayAdapter.clear()
//            arrayAdapter.addAll(userNames)
//        })

        // NEW 2
        val userListView = findViewById<ListView>(R.id.userlist)


        // Observe the LiveData and update the adapter when data changes
        userViewModel.allUsers.observe(this, Observer { users ->
            val adapter = UserListAdapter(this, users)
            userListView.adapter = adapter

            // Assuming 'name' is the property you want to display
            //val userNames = users.map { it.name }

            adapter.updateData(users)
        })
    }
}