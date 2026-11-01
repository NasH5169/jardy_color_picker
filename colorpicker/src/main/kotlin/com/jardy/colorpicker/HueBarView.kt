package com.jardy.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet

/** Hue 360° at the top down to 0° at the bottom. */
class HueBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : VerticalBarView(context, attrs) {

    private val paint = Paint()

    internal var hue: Float
        get() = 360f * (1f - fraction)
        set(v) { fraction = 1f - v / 360f }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val colors = IntArray(7) { Color.HSVToColor(floatArrayOf(360f - it * 60f, 1f, 1f)) }
        paint.shader = LinearGradient(0f, 0f, 0f, h.toFloat(), colors, null, Shader.TileMode.CLAMP)
    }

    override fun drawBar(canvas: Canvas, width: Float, height: Float) {
        canvas.drawRect(0f, 0f, width, height, paint)
    }
}
