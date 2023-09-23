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
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.ui.UserDetailsActivity
import java.math.RoundingMode
import java.text.DecimalFormat

class ConsumableListAdapter(private val context: Context, private var consumableList: List<Consumable>) : BaseAdapter() {
    override fun getCount(): Int {
        return consumableList.size
    }

    override fun getItem(position: Int): Any {
        return consumableList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Custom method to update the data in the adapter
    fun updateData(newList: List<Consumable>) {
        consumableList = newList
        notifyDataSetChanged()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val consumable = getItem(position) as Consumable
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.list_item_consumable, null)

        val consumableNameTextView = view.findViewById<TextView>(R.id.consumableName)

        // Set user data to views
        consumableNameTextView.text = consumable.name // Replace with user's name

//        val listItemLayout = view.findViewById<ConstraintLayout>(R.id.consumable_list_item)
//        listItemLayout.setOnClickListener {
//            // Handle item click here
//            val intent = Intent(context, ConsumableDetailsActivity::class.java) // like a navigator
//            intent.putExtra("consumable", consumable) // Pass the selected user's data
//            context.startActivity(intent)
//        }

        return view
    }
}
