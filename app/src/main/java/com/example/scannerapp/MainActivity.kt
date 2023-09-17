package com.example.scannerapp

import android.os.Bundle
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
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.ui.theme.ScannerAppTheme
import com.example.scannerapp.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {

  //  private lateinit var mUserViewModel: UserViewModel
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    setContent {
      ScannerAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
//          insertIntoDatabase()
          Greeting("Group 7")
        }
      }
    }
  }

  // For testing only
//  private fun insertIntoDatabase() {
//    val user = User(0, "Justin")
//    mUserViewModel.addUser(user)
//  }

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
}
