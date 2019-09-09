package ru.ls.cardriver.domain.model

import android.graphics.Point

data class PointLocation(val x: Float, val y: Float) {

	fun toPoint(): Point = Point(x.toInt(), y.toInt())
}