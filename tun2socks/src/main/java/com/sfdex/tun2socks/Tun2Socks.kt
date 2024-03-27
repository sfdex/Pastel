package com.sfdex.tun2socks

class Tun2Socks {
    external fun main(fd: Int, logPath: String)

    companion object {
        // Used to load the 'tun2socks' library on application startup.
        init {
            System.loadLibrary("tun2socks")
        }
    }
}