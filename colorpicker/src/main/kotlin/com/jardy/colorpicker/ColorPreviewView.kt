package com.jardy.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/** Small swatch showing a color, alpha included, over a checkerboard. */
class ColorPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val checker = Checkerboard.createPaint((6 * resources.displayMetrics.density).toInt())
    private val fill = Paint()

    internal var color = 0
        set(v) { field = v; fill.color = v; invalidate() }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawRect(0f, 0f, w, h, checker)
        canvas.drawRect(0f, 0f, w, h, fill)
    }
}
