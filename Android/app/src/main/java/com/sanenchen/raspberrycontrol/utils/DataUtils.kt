package com.sanenchen.raspberrycontrol.utils

import android.content.Context

open class DataUtils {
    companion object {
        var raspiIP = "192.168.1.2"
        var raspiPort = "8080"
        val raspiURL = "http://$raspiIP:$raspiPort"
        var recycleTime: Long = 500
    }
}