package com.sfdex.pastel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import com.sfdex.tun2socks.Tun2Socks
import kotlin.concurrent.thread

const val ACTION_CONNECT = "com.sfdex.pastel.connect"
const val ACTION_DISCONNECT = "com.sfdex.pastel.disconnect"

class PastelService : VpnService() {
    private var connectionThread: Thread? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null
    private var tun2socks: Tun2Socks? = null

    private var isRunning = false
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
        if (isRunning) return

        val connectionThread = thread {
            val builder = Builder()
            builder.apply {
                setMtu(1500)
                addRoute("0.0.0.0", 0)
                addAddress("192.168.2.1", 30)
                addDnsServer("192.168.2.2")
                addAllowedApplication("com.sfdex.net")
            }

            parcelFileDescriptor = builder.establish()
            if (parcelFileDescriptor != null) {
                Log.d(TAG, "established fd: ${parcelFileDescriptor!!.fd}")
                val logPath = "${filesDir.absolutePath}/hello.txt"
                Log.d(TAG, "logPath: $logPath")
                tun2socks = Tun2Socks()
                isRunning = true
                tun2socks?.main(parcelFileDescriptor!!.fd, logPath)
                isRunning = false
                Log.d(TAG, "tun2socks ended")
            }
        }

        this.connectionThread = connectionThread
        updateNotification("Connected")
    }

    private fun disconnect() {
        updateNotification("Disconnected")
        try {
            parcelFileDescriptor?.close()
            connectionThread?.interrupt()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tun2socks = null
            parcelFileDescriptor = null
            connectionThread = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            isRunning = false
            stopSelf()
        }
    }

    private fun updateNotification(msg: String) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel("Pastel") == null) {
                manager.createNotificationChannel(
                    NotificationChannel(
                        "Pastel",
                        "Pastel service",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
            }

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

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }
}

private const val TAG = "PastelService"