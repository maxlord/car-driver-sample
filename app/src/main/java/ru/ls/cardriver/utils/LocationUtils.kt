package ru.ls.cardriver.utils

import ru.ls.cardriver.domain.model.PointItem
import kotlin.math.abs
import kotlin.math.atan2

class LocationUtils {

	companion object {
		fun calcRotationAngleInDegrees(centerPt: PointItem, targetPt: PointItem): Double {
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

		/**
		 * Shortest distance (angular) between two angles.
		 * It will be in range [0, 180].
		 */
		fun distance(alpha: Int, beta: Int): Int {
			val phi = abs(beta - alpha) % 360 // This is either the distance or 360 - distance
			return if (phi > 180) 360 - phi else phi
		}

		fun isNegativeDirection(alpha: Int, beta: Int): Boolean {
			return alpha - beta in 0..180 || alpha - beta <= -180 && alpha - beta >= -360
		}
	}
}