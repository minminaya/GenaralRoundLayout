package com.minminaya.policy

import android.graphics.Canvas

/**
 * 圆角策略接口
 * @author minminaya
 * @email minminaya@gmail.com
 * @time Created by 2019/6/8 0:27
 *
 */
interface IRoundViewPolicy {
    fun beforeDispatchDraw(canvas: Canvas?)
    fun afterDispatchDraw(canvas: Canvas?)
    fun onLayout(left: Int, top: Int, right: Int, bottom: Int)
    fun setCornerRadius(cornerRadius: Float)
}