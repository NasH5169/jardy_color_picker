package com.jardy.colorpicker.sample

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jardy.colorpicker.JardyColorPickerDialog

class MainActivity : AppCompatActivity() {

    private var color = Color.argb(200, 0x2E, 0xCC, 0x40)
    private lateinit var swatch: View
    private lateinit var label: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        color = savedInstanceState?.getInt("color") ?: color
        val dp = resources.displayMetrics.density

        swatch = View(this).apply {
            layoutParams = LinearLayout.LayoutParams((200 * dp).toInt(), (80 * dp).toInt())
        }
        label = TextView(this).apply {
            textSize = 18f; setPadding(
            0,
            (12 * dp).toInt(),
            0,
            (12 * dp).toInt()
        )
        }
        val button = Button(this).apply {
            text = "Choisir une couleur"
            setOnClickListener { pick() }
        }

        val customButton = Button(this).apply {
            text = "Version personnalisée"
            setOnClickListener { pick(custom = true) }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(swatch)
            addView(label)
            addView(button)
            addView(customButton)
        })
        update()
    }

    private fun pick(custom: Boolean = false) {
        JardyColorPickerDialog.Builder(this)
            .setTitle("Ligne de navigation")
            .setInitialColor(color)
            .apply {
                if (custom) {
                    setBackgroundColor(Color.parseColor("#12263A"))
                    setTextColor(Color.parseColor("#F2E3C6"))
                    setButtonTextColor(Color.parseColor("#F2E3C6"))
                }
            }
            .setOnColorSelected { color = it; update() }
            .show()
    }

    private fun update() {
        swatch.setBackgroundColor(color)
        label.text = String.format("#%08X", color)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("color", color)
    }
}
