package ru.ls.cardriver.utils

import android.graphics.Point
import kotlin.math.atan2

class LocationUtils {

	companion object {
		fun calcRotationAngleInDegrees(centerPt: Point, targetPt: Point): Double {
			val theta = atan2(
				-(targetPt.y - centerPt.y).toDouble(),
				(targetPt.x - centerPt.x).toDouble()
			)
			var angle = 90 - Math.toDegrees(theta)
			if (angle < 0) {
				angle += 360.0
			}
			return angle
		}
	}
}