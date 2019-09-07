package ru.ls.cardriver.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DestinationView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

	private val fillColor = Color.GREEN
	private lateinit var fillPaint: Paint

	init {
		isFocusable = true
		isFocusableInTouchMode = true
		setupPaint()
	}

	private fun setupPaint() {
		fillPaint = Paint().apply {
			color = fillColor
			isAntiAlias = true
			style = Paint.Style.FILL
		}
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.let { canvas ->
			val x = measuredWidth.toFloat() / 2
			val y = measuredHeight.toFloat() / 2
			val radius = measuredWidth.toFloat() / 2

			canvas.drawCircle(x, y, radius, fillPaint)
		}
	}
}