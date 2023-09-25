package com.example.scannerapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.scannerapp.R
import com.example.scannerapp.adapters.UserListAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputEditText

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

        // Show the dialog when the fab is clicked
        val createUserFAB = findViewById<FloatingActionButton>(R.id.fab)
        createUserFAB.setOnClickListener {
            showCreateUserDialog()
        }
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

    private fun showCreateUserDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_user, null)
        builder.setView(dialogView)

        val textInputEditTextName = dialogView.findViewById<TextInputEditText>(R.id.textInputEditTextName)
        val switchStatus = dialogView.findViewById<MaterialSwitch>(R.id.switchStatus)
        val buttonSave = dialogView.findViewById<MaterialButton>(R.id.buttonSave)
        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)

        val dialog = builder.create()

        buttonSave.setOnClickListener {
            val userName = textInputEditTextName.text.toString().trim()
            val switchStatus = switchStatus.isChecked // true for enabled, false for disabled

            val userStatus: Int = if (switchStatus) {
                1
            } else {
                0
            } // 1 for enabled, 0 for disabled

            if (userName.isNotEmpty()) {

                val newUser = User(userId = 0, name = userName, status = userStatus)

                // use the function in ViewModel to add the user
                userViewModel.addUser(newUser)

                // display success message
                Toast.makeText(
                    this@ListUsersActivity,
                    "User created successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()

            } else {
                // display an error message if the user name is empty
                Toast.makeText(
                    this@ListUsersActivity,
                    "Please enter a name.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        closeButton.setOnClickListener {
            // User clicked the close button, dismiss the dialog
            dialog.dismiss()
        }

        dialog.show()
    }

}