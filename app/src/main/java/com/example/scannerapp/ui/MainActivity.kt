package com.example.scannerapp.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.scannerapp.R
import com.example.scannerapp.theme.ScannerAppTheme

class MainActivity : AppCompatActivity() {

  //  private lateinit var mUserViewModel: UserViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_base)

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
//    val user = User(name = "Justin", status = 0)
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
