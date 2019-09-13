package ru.ls.cardriver.data.interactor

import io.reactivex.Maybe
import io.reactivex.Single
import ru.ls.cardriver.domain.model.PointItem
import ru.ls.cardriver.utils.LocationUtils

class LocationInteractorImpl : LocationInteractor {

	override fun calcRotationAngleInDegrees(
		fromPoint: PointItem,
		toPoint: PointItem
	): Single<Double> {
		return Single.just(LocationUtils.calcRotationAngleInDegrees(fromPoint, toPoint))
	}

	override fun generateAngles(
		fromAngle: Int,
		toAngle: Int
	): Maybe<List<Int>> {
		return Maybe.create { emitter ->
			val angleDiff = LocationUtils.distance(fromAngle, toAngle)
			val negativeDirection =
				LocationUtils.isNegativeDirection(fromAngle, toAngle)
			if (angleDiff > 0) {
				val angles = generateAnglesInternal(fromAngle, toAngle, negativeDirection)
				emitter.onSuccess(angles)
			} else {
				emitter.onComplete()
			}
		}
	}

	private fun generateAnglesInternal(
		fromAngle: Int,
		toAngle: Int,
		negativeDirection: Boolean
	): List<Int> {
		val angles = arrayListOf<Int>()
		var angle = fromAngle
		if (negativeDirection) {
			while (angle != toAngle) {
				angles.add(angle)
				angle--
				if (angle < 0) {
					angle += 360
				}
			}
			angles.add(angle)
		} else {
			while (angle != toAngle) {
				angles.add(angle)
				angle++
				if (angle >= 360) {
					angle = 360 - angle
				}
			}
			angles.add(angle)
		}
		return angles
	}
}