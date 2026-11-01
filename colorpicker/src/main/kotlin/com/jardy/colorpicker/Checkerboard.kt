package com.jardy.colorpicker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.BitmapShader

/** Transparency checkerboard paint, shared by every view that shows alpha. */
internal object Checkerboard {
    fun createPaint(cellPx: Int): Paint {
        val size = cellPx * 2
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        canvas.drawColor(Color.WHITE)
        paint.color = Color.rgb(0xB0, 0xB0, 0xB0)
        canvas.drawRect(0f, 0f, cellPx.toFloat(), cellPx.toFloat(), paint)
        canvas.drawRect(cellPx.toFloat(), cellPx.toFloat(), size.toFloat(), size.toFloat(), paint)
        return Paint().apply { shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT) }
    }
}
