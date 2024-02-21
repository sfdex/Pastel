package com.sfdex.tun2socks

class Tun2Socks {

    /**
     * A native method that is implemented by the 'tun2socks' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun main(fd: Int, logPath: String)
    external fun testNum(fd: Int): Int
    external fun testStr(logPath: String): String
    external fun testStrWithLen(str: String, len: Int): String

    companion object {
        // Used to load the 'tun2socks' library on application startup.
        init {
            System.loadLibrary("tun2socks")
        }
    }
}