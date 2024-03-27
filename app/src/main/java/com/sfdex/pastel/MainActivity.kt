package com.sfdex.pastel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sfdex.pastel.ui.theme.PastelTheme

class MainActivity : ComponentActivity() {
    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(object :
            ActivityResultContract<Intent, Int>() {
            override fun createIntent(context: Context, input: Intent): Intent {
                return input
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Int {
                return if (resultCode == Activity.RESULT_OK) {
                    1
                } else {
                    0
                }
            }

        }) {
            if (it > 0) {
                startService(
                    Intent(this, PastelService::class.java).setAction(
                        ACTION_CONNECT
                    )
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PastelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UI(this)
                }
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }

    fun startSvc() {
        VpnService.prepare(this)?.let {
            activityResultLauncher.launch(it)
            return
        }

        startService(
            Intent(this, PastelService::class.java).setAction(
                ACTION_CONNECT
            )
        )
    }

    fun stopSvc() {
        startService(
            Intent(this, PastelService::class.java).setAction(
                ACTION_DISCONNECT
            )
        )
    }
}

@Composable
fun UI(activity: MainActivity) {
    Box {
        Text(
            text = "Pastel",
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        )
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(
                modifier = Modifier.width(90.dp),
                onClick = { activity.startSvc() }) {
                Text(text = "Start")
            }

            Button(
                modifier = Modifier
                    .width(90.dp)
                    .padding(top = 20.dp, bottom = 30.dp),
                onClick = { activity.stopSvc() }) {
                Text(text = "Stop")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UiPreview() {
    PastelTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            UI(MainActivity())
        }
    }
}