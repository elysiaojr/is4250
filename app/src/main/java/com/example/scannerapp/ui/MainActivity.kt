package com.example.scannerapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scannerapp.R
import com.example.scannerapp.theme.ScannerAppTheme
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions


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

  // Composable function to show a toast message
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
}
