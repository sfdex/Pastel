package com.sfdex.pastel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sfdex.pastel.ui.theme.PastelTheme
import com.sfdex.tun2socks.Tun2Socks

lateinit var tun2Socks: Tun2Socks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PastelTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android", this)
                }
            }
        }

        tun2Socks = Tun2Socks()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            startService(serviceIntent)
        }
    }

    private val serviceIntent
        get() =
            Intent(this, PastelService::class.java)
}

@Composable
fun Greeting(name: String, context: Activity, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Button(onClick = {
            val str = tun2Socks.stringFromJNI()
            Log.d(TAG, "Tun2Socks::stringFromJNI: $str")
        }) {
            Text(text = "HelloJNI")
        }

        Button(onClick = {
            tun2Socks.main(0, "80")
        }) {
            Text(text = "Main")
        }

        Button(onClick = {
            val num = tun2Socks.testNum(1001)
            Log.d(TAG, "testNum: $num")
        }) {
            Text(text = "testNum")
        }

        Button(onClick = {
            val str = tun2Socks.testStr("/sdcard/")
            Log.d(TAG, "testCStr: $str")
        }) {
            Text(text = "testStr")
        }

        Button(onClick = {
            val str = tun2Socks.testStrWithLen("/sdcard/", 8)
            Log.d(TAG, "testCStrWithLen: $str")
        }) {
            Text(text = "testStrWithLen")
        }

        Button(onClick = {
            val intent = VpnService.prepare(context)
            if (intent != null) {
                context.startActivityForResult(intent, 0)
            } else {
                context.startService(Intent(context, PastelService::class.java))
            }
        }) {
            Text(text = "startService")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PastelTheme {
//        Greeting("Android")
    }
}

private const val TAG = "MainActivity"