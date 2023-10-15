package com.example.scannerapp.adapters

import android.content.Context
import android.content.Intent
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.scannerapp.R
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.ui.UserDetailsActivity
import java.math.RoundingMode
import java.text.DecimalFormat

class UserListAdapter(private val context: Context, private var userList: List<User>) : BaseAdapter() {
    override fun getCount(): Int {
        return userList.size
    }

    override fun getItem(position: Int): Any {
        return userList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Custom method to update the data in the adapter
    fun updateData(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val user = getItem(position) as User
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.list_item_user, null)

        val usernameTextView = view.findViewById<TextView>(R.id.username)

        // Set user data to views
        usernameTextView.text = user.name // Replace with user's name

        // Handles navigation
        val listItemLayout = view.findViewById<ConstraintLayout>(R.id.user_list_item)
        listItemLayout.setOnClickListener {
            // Handle item click here
            val intent = Intent(context, UserDetailsActivity::class.java) // like a navigator
            intent.putExtra("user", user) // Pass the selected user's data
            context.startActivity(intent)
        }
        return view
    }
}
