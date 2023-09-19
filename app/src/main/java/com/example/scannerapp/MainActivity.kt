package com.example.scannerapp

import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.ui.theme.ScannerAppTheme
import com.example.scannerapp.viewmodels.UserViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
class MainActivity : ComponentActivity() {

    private val barcodeLauncher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
          val data = result.data
          val scanResult = data?.getStringExtra("SCAN_RESULT")
          if (scanResult == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(this, "Scanned: $scanResult", Toast.LENGTH_LONG).show();
          }
        } else {
          // Handle the case when scanning was canceled or failed.
          Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
        }
      }

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
          BarcodeScanner()
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
  }    // Composable function to show a toast message
//  @Composable
//  private fun showToast(message: String) {
//    val context = LocalContext.current
//    DisposableEffect(Unit) {
//      val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
//      toast.show()
//      onDispose {
//        toast.cancel()
//      }
//    }
//  }

  // Composable function for barcode scanning
  @Composable
  private fun BarcodeScanner() {
    val context = this
    Button(
      onClick = {
        val scanOptions = ScanOptions() // Customize scan options as needed
        val intent = Intent(context, CaptureActivity::class.java)
        intent.action = "com.google.zxing.client.android.SCAN"
        barcodeLauncher.launch(intent)
      },
      modifier = Modifier.padding(16.dp)
    ) {
      Text(text = "Scan Barcode")
    }
  }
}
