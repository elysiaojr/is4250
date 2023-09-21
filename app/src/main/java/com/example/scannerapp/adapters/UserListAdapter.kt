package com.example.scannerapp.adapters

import android.content.Context
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.scannerapp.R
//import com.example.scannerapp.helpers.ModifyFood
import com.example.scannerapp.database.entities.User
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

        return view
    }
}
