package com.jardy.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Base for the vertical sliders. [fraction] runs from 0 (top) to 1 (bottom);
 * subclasses decide how it maps to a hue or an alpha and how the bar is painted.
 */
abstract class VerticalBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    internal var onFractionChanged: ((Float) -> Unit)? = null

    protected val density = resources.displayMetrics.density

    protected var fraction = 0f
        set(v) { field = v; invalidate() }

    private val markerFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 3f * density; color = Color.WHITE
    }
    private val markerOutline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 1f * density; color = Color.argb(160, 0, 0, 0)
    }
    private val markerRect = RectF()

    protected abstract fun drawBar(canvas: Canvas, width: Float, height: Float)

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        drawBar(canvas, w, h)
        val y = fraction * h
        val half = 4f * density
        markerRect.set(-2f * density, y - half, w + 2f * density, y + half)
        canvas.drawRoundRect(markerRect, 2f * density, 2f * density, markerOutline)
        canvas.drawRoundRect(markerRect, 2f * density, 2f * density, markerFill)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> parent?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_UP -> { performClick(); return true }
            else -> return true
        }
        fraction = (event.y / height).coerceIn(0f, 1f)
        onFractionChanged?.invoke(fraction)
        return true
    }

    override fun performClick(): Boolean = super.performClick()
}
