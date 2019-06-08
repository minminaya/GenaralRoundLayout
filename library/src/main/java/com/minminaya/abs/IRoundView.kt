package com.minminaya.abs

/**
 * 通用圆角布局抽象接口
 * @author minminaya
 * @email minminaya@gmail.com
 * @time Created by 2019/6/7 21:51
 *
 */
interface IRoundView {
    fun setCornerRadius(cornerRadius: Float)
    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
}