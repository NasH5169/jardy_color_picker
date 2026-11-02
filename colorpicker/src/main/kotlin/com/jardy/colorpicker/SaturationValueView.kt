package com.jardy.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/** Square area: saturation grows to the right, brightness grows to the top. */
class SaturationValueView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    internal var onChanged: ((saturation: Float, value: Float) -> Unit)? = null

    internal var hue = 0f
        set(v) {
            field = v
            rebuildShader()
            invalidate()
        }
    internal var saturation = 1f
        set(v) { field = v; invalidate() }
    internal var value = 1f
        set(v) { field = v; invalidate() }

    private val density = resources.displayMetrics.density
    private val huePaint = Paint()
    private val shadePaint = Paint()
    private val cursorFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 2f * density; color = Color.WHITE
    }
    private val cursorOutline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 1f * density; color = Color.argb(160, 0, 0, 0)
    }
    private val cursorRadius = 10f * density

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        // Square by default; an exact height (landscape layout) is honored as is.
        val h = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            w
        }
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) = rebuildShader()

    private fun rebuildShader() {
        if (width == 0) return
        val pure = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        huePaint.shader = LinearGradient(0f, 0f, width.toFloat(), 0f, Color.WHITE, pure, Shader.TileMode.CLAMP)
        shadePaint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP)
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawRect(0f, 0f, w, h, huePaint)
        canvas.drawRect(0f, 0f, w, h, shadePaint)
        val cx = saturation * w
        val cy = (1f - value) * h
        canvas.drawCircle(cx, cy, cursorRadius + 1f * density, cursorOutline)
        canvas.drawCircle(cx, cy, cursorRadius, cursorFill)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> parent?.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_UP -> { performClick(); return true }
            else -> return true
        }
        saturation = (event.x / width).coerceIn(0f, 1f)
        value = 1f - (event.y / height).coerceIn(0f, 1f)
        onChanged?.invoke(saturation, value)
        return true
    }

    override fun performClick(): Boolean = super.performClick()
}
