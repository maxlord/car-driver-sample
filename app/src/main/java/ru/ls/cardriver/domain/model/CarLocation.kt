package ru.ls.cardriver.domain.model

import android.graphics.Point

data class CarLocation(val x: Int, val y: Int) {

	fun toPoint(): Point = Point(x, y)
}