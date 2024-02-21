package com.sfdex.pastel

import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import android.util.Log
import com.sfdex.tun2socks.Tun2Socks

class PastelService : VpnService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder = Builder()
        builder.apply {
            addAddress("192.168.2.2", 24)
            addRoute("0.0.0.0", 0)
            addDnsServer("192.168.1.1")
        }

        val localTunnel = builder.establish()
        if (localTunnel != null) {
            Log.d(TAG, "established fd: ${localTunnel.fd}")
            val logPath = "${filesDir.absolutePath}/hello.txt"
            Log.d(TAG, "logPath: $logPath")
            Tun2Socks().main(localTunnel.fd, logPath)
            Log.d(TAG, "connection lost")
        }
        Log.d(TAG, "end")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onRevoke() {
        super.onRevoke()
        Log.d(TAG, "onRevoke: ")
    }
}

private const val TAG = "PastelService"