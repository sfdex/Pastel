package com.sfdex.pastel

import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.sfdex.tun2socks.Tun2Socks
import kotlin.concurrent.thread

class PastelService : VpnService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder = Builder()
        builder.apply {
            setMtu(1500)
            addAddress("192.168.2.2", 24)
            addRoute("0.0.0.0", 0)
            addDnsServer("192.168.1.1")
            addAllowedApplication("com.sfdex.net")
        }

        val localTunnel = builder.establish()
        if (localTunnel != null) {
            Log.d(TAG, "established fd: ${localTunnel.fd}")
            val logPath = "${filesDir.absolutePath}/hello.txt"
            Log.d(TAG, "logPath: $logPath")
            thread {
                Tun2Socks().main(localTunnel.fd, logPath)
            }
            Toast.makeText(this, "Pastel started", Toast.LENGTH_SHORT).show()
//            Toast.makeText(this, "Pastel ended 1", Toast.LENGTH_SHORT).show()
//            Log.d(TAG, "connection lost")
        }
//        Log.d(TAG, "end")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onRevoke() {
        super.onRevoke()
        Log.d(TAG, "onRevoke: ")
        Toast.makeText(this, "Pastel ended 2", Toast.LENGTH_SHORT).show()
    }
}

private const val TAG = "PastelService"