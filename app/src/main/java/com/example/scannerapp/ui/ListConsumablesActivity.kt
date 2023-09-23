package com.example.scannerapp.ui

import android.os.Bundle
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.scannerapp.R
import com.example.scannerapp.adapters.ConsumableListAdapter
import com.example.scannerapp.ui.ui.theme.ScannerAppTheme
import com.example.scannerapp.viewmodels.ConsumableViewModel

class ListConsumablesActivity : ComponentActivity() {
    private lateinit var consumableViewModel: ConsumableViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_consumables)

        consumableViewModel = ViewModelProvider(this).get(ConsumableViewModel::class.java)
        val consumableListView = findViewById<ListView>(R.id.consumablelist)

        // Observe the LiveData and update the adapter when data changes
        consumableViewModel.allConsumables.observe(this, Observer { consumables ->
            val adapter = ConsumableListAdapter(this, consumables)
            consumableListView.adapter = adapter

            adapter.updateData(consumables)
        })
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScannerAppTheme {
        Greeting("Android")
    }
}