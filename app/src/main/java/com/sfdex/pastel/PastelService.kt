package com.sfdex.pastel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.sfdex.tun2socks.Tun2Socks
import kotlin.concurrent.thread

const val ACTION_CONNECT = "com.sfdex.pastel.connect"
const val ACTION_DISCONNECT = "com.sfdex.pastel.disconnect"

class PastelService : VpnService() {
    private var connectionThread: Thread? = null
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate() {
        super.onCreate()
        pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return if (intent?.action?.equals(ACTION_CONNECT) == true) {
            connect()
            START_STICKY
        } else {
            disconnect()
            START_NOT_STICKY
        }
    }

    private fun connect() {
        val connectionThread = thread {
            val builder = Builder()
            builder.apply {
                setMtu(1500)
                addAddress("10.0.0.1", 24)
                addRoute("0.0.0.0", 0)
                addDnsServer("192.168.1.1")
                addAllowedApplication("com.sfdex.net")
            }

            val localTunnel = builder.establish()
            if (localTunnel != null) {
                Log.d(TAG, "established fd: ${localTunnel.fd}")
                val logPath = "${filesDir.absolutePath}/hello.txt"
                Log.d(TAG, "logPath: $logPath")
                Tun2Socks().main(localTunnel.fd, logPath)
            }
        }

        this.connectionThread = connectionThread
        updateNotification("Connected")
    }

    private fun disconnect() {
        updateNotification("Disconnected")
        connectionThread?.interrupt()
        connectionThread = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun updateNotification(msg: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    "Pastel",
                    "Pastel service",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )

            startForeground(
                1, Notification.Builder(this, "Pastel")
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build()
            )
        } else {
            startForeground(
                1, Notification.Builder(this)
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build()
            )
        }
    }

    override fun onRevoke() {
        super.onRevoke()
        Log.d(TAG, "onRevoke: ")
        Toast.makeText(this, "Pastel ended 2", Toast.LENGTH_SHORT).show()
    }
}

private const val TAG = "PastelService"