package com.minminaya

import android.content.Context

/**
 * 密度转换像素
 * @param dipValue dp值
 * @return 像素
 */
fun dip2px(context: Context, dipValue: Float): Float {
    val displayMetrics = context.applicationContext.resources.displayMetrics
    return dipValue * displayMetrics.density + 0.5f
}