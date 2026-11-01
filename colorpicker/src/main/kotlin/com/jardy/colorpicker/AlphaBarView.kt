package com.jardy.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet

/** Opaque at the top, transparent at the bottom, over a checkerboard. */
class AlphaBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : VerticalBarView(context, attrs) {

    private val checker = Checkerboard.createPaint((6 * density).toInt())
    private val gradient = Paint()

    internal var alphaValue: Int
        get() = Math.round(255f * (1f - fraction))
        set(v) { fraction = 1f - v / 255f }

    internal var color = Color.RED
        set(v) {
            field = v
            rebuildShader()
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) = rebuildShader()

    private fun rebuildShader() {
        if (height == 0) return
        val opaque = color or 0xFF000000.toInt()
        val clear = color and 0x00FFFFFF
        gradient.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), opaque, clear, Shader.TileMode.CLAMP)
    }

    override fun drawBar(canvas: Canvas, width: Float, height: Float) {
        canvas.drawRect(0f, 0f, width, height, checker)
        canvas.drawRect(0f, 0f, width, height, gradient)
    }
}
