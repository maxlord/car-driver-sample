package ru.ls.cardriver.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ru.ls.cardriver.extensions.toPx

class CarView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

	private val fillColor = Color.parseColor("#FFA500")
	private val strokeColor = Color.BLACK
	private lateinit var fillPaint: Paint
	private lateinit var strokePaint: Paint

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
		strokePaint = Paint().apply {
			color = strokeColor
			isAntiAlias = true
			strokeWidth = 3.toPx().toFloat()
			style = Paint.Style.STROKE
		}
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.let { canvas ->
			val left = 0f
			val top = 0f
			val right = measuredWidth.toFloat()
			val bottom = measuredHeight.toFloat()

			canvas.drawRect(left, top, right, bottom, fillPaint)
			canvas.drawRect(left, top, right, bottom, strokePaint)
		}
	}
}