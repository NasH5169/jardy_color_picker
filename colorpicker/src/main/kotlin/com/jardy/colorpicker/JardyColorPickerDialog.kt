package com.jardy.colorpicker

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.EditText

/**
 * Color picker dialog (#AARRGGBB). It carries its own framework-based theme, so it
 * looks right whatever theme the host app uses.
 *
 * ```
 * JardyColorPickerDialog.Builder(activity)
 *     .setTitle("Navigation line")
 *     .setInitialColor(current)
 *     .setOnColorSelected { color -> ... }
 *     .show()
 * ```
 */
class JardyColorPickerDialog private constructor(private val builder: Builder) {

    class Builder(context: Context) {
        internal val context: Context = context
        internal var title: CharSequence? = null
        internal var initialColor: Int = Color.RED
        internal var alphaEnabled = true
        internal var themeResId = R.style.JardyColorPicker_Dialog
        internal var onSelected: ((Int) -> Unit)? = null
        internal var onCancelled: (() -> Unit)? = null

        fun setTitle(title: CharSequence?) = apply { this.title = title }
        fun setTitle(titleResId: Int) = apply { this.title = context.getString(titleResId) }
        fun setInitialColor(color: Int) = apply { initialColor = color }

        /** When false, the alpha bar is hidden and the result is always fully opaque. */
        fun setAlphaEnabled(enabled: Boolean) = apply { alphaEnabled = enabled }

        /** Replaces the default theme; it should derive from a framework Material dialog theme. */
        fun setTheme(themeResId: Int) = apply { this.themeResId = themeResId }

        fun setOnColorSelected(listener: ((Int) -> Unit)?) = apply { onSelected = listener }
        fun setOnCancelled(listener: (() -> Unit)?) = apply { onCancelled = listener }

        fun build(): AlertDialog = JardyColorPickerDialog(this).create()
        fun show(): AlertDialog = build().also { it.show() }
    }

    private fun create(): AlertDialog {
        val themed = ContextThemeWrapper(builder.context, builder.themeResId)
        val content = LayoutInflater.from(themed).inflate(R.layout.jcp_dialog_content, null)
        val satVal = content.findViewById<SaturationValueView>(R.id.jcp_sat_val)
        val hueBar = content.findViewById<HueBarView>(R.id.jcp_hue)
        val alphaBar = content.findViewById<AlphaBarView>(R.id.jcp_alpha)
        val preview = content.findViewById<ColorPreviewView>(R.id.jcp_preview)
        val hex = content.findViewById<EditText>(R.id.jcp_hex)

        // HSV is kept as the source of truth so hue survives saturation/value hitting 0.
        val hsv = FloatArray(3)
        var alpha = 255
        var updatingHex = false

        fun currentColor(): Int = Color.HSVToColor(if (builder.alphaEnabled) alpha else 255, hsv)

        fun formatted(color: Int) = String.format("#%08X", color)

        fun refresh(fromHex: Boolean) {
            val color = currentColor()
            satVal.hue = hsv[0]
            satVal.saturation = hsv[1]
            satVal.value = hsv[2]
            hueBar.hue = hsv[0]
            alphaBar.color = Color.HSVToColor(hsv)
            alphaBar.alphaValue = alpha
            preview.color = color
            if (!fromHex) {
                updatingHex = true
                hex.setText(formatted(color))
                updatingHex = false
            }
        }

        fun load(color: Int) {
            Color.colorToHSV(color, hsv)
            alpha = if (builder.alphaEnabled) Color.alpha(color) else 255
        }

        load(builder.initialColor)
        if (!builder.alphaEnabled) {
            alphaBar.visibility = android.view.View.GONE
            hex.filters = arrayOf(InputFilter.LengthFilter(7))
        }

        satVal.onChanged = { s, v -> hsv[1] = s; hsv[2] = v; refresh(false) }
        hueBar.onFractionChanged = { hsv[0] = hueBar.hue.coerceAtMost(359.99f); refresh(false) }
        alphaBar.onFractionChanged = { alpha = alphaBar.alphaValue; refresh(false) }

        hex.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable) {
                if (updatingHex) return
                val text = s.toString()
                val expected = if (builder.alphaEnabled) 9 else 7
                if (text.length != expected || !text.startsWith("#")) return
                val color = runCatching { Color.parseColor(text) }.getOrNull() ?: return
                // Keep the hue when the typed color is grey/black, where it is undefined.
                val previousHue = hsv[0]
                load(color)
                if (hsv[1] == 0f || hsv[2] == 0f) hsv[0] = previousHue
                refresh(true)
            }
        })

        refresh(false)

        return AlertDialog.Builder(themed)
            .setTitle(builder.title)
            .setView(content)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                builder.onSelected?.invoke(currentColor())
            }
            .setNegativeButton(android.R.string.cancel) { _: DialogInterface, _: Int ->
                builder.onCancelled?.invoke()
            }
            .setOnCancelListener { builder.onCancelled?.invoke() }
            .create()
    }
}
