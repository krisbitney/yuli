package io.github.krisbitney.yuli.api

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import com.github.instagram4j.instagram4j.IGAndroidDevice
import kotlin.math.roundToInt
import kotlin.math.sqrt

class DeviceInfoHelper(context: Context) {

    private val displayMetrics: DisplayMetrics = context.resources.displayMetrics

    fun getDeviceInformation(): IGAndroidDevice {
        val androidVersion = Build.VERSION.SDK_INT.toString()
        val androidRelease = Build.VERSION.RELEASE
        val dpi = calculateExactDpi(displayMetrics.xdpi, displayMetrics.ydpi).toString() + "dpi"
        val displayResolution = "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}"
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val device = Build.DEVICE
        val cpu = Build.HARDWARE

        val formatted = "$androidVersion/$androidRelease; $dpi; $displayResolution; $manufacturer; $model; $device; $cpu"
        return IGAndroidDevice(formatted)
    }

    private fun calculateExactDpi(xdpi: Float, ydpi: Float): Int {
        val dpi = sqrt(xdpi * xdpi + ydpi * ydpi)
        return (dpi / 10).roundToInt() * 10
    }
}