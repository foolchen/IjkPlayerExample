package com.foolchen.ijkplayer.example.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

fun View.resize(width: Int, height: Int) {
    val lp = layoutParams
    lp.width = width
    lp.height = height
    layoutParams = lp
}

/**
 * 使用上下文对象获取当前手机屏幕宽度
 */
fun Context.getScreenWidth(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return metrics.widthPixels
}

/**
 * 使用上下文对象获取当前手机屏幕高度
 */
fun Context.getScreenHeight(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val metrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        display.getRealMetrics(metrics)
    } else {
        display.getMetrics(metrics)
    }
    return metrics.heightPixels
}