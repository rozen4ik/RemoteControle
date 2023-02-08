package ru.ertel.remotecontrole.data

class DataSourceDevice {
    private var devices = ArrayList<String>()
    private var deviceArray = ArrayList<String>()
    private var strDevice: String = ""

    fun setDeviceArray(message: String) {
        var msg = message.substringAfter("<answer>")
        msg = msg.substringBefore("</answer>")
        devices = msg.split("/>").toList() as ArrayList<String>

        for (i in devices) {
            if (i.contains("<device", ignoreCase = true)) {
                strDevice += "${getDevice(i)}, "
            }
        }

        strDevice = strDevice.substringBeforeLast(",")

        deviceArray = strDevice.split(", ").toList() as ArrayList<String>
    }

    fun getDeviceArray(): ArrayList<String> {
        return deviceArray
    }

    private fun getDevice(message: String): String {
        var id = message.substringAfter("id=\"")
        id = id.substringBefore("\"")
        var name = message.substringAfter("name=\"")
        name = name.substringBefore("\"")
        return "$id:$name"
    }
}