package ru.ls.cardriver.presentation.main

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointLocation
import ru.ls.cardriver.utils.LocationUtils
import kotlin.math.abs

class MainPresenter : MvpBasePresenter<MainView>() {

	private val disposables = CompositeDisposable()
	private var containerWidth: Int = 0
	private var containerHeight: Int = 0
	private var currentCarLocation: CarLocation = CarLocation(containerWidth, containerHeight)
	private var currentDestinationLocation: PointLocation = PointLocation(0, 0)
	private var currentCarAngle: Float = 0f
	private var isDriving = false

	fun init(width: Int, height: Int) {
		containerWidth = width
		containerHeight = height
		currentCarLocation = CarLocation(containerWidth / 2, containerHeight / 2)

		ifViewAttached { v ->
			disposables.add(v.destinationClicks().subscribe(this::onHandleDestinationClick))
			disposables.add(v.onCarRotationEnds().subscribe(this::onCarRotationEnd))
			disposables.add(v.onCarMovingEnds().subscribe(this::onCarMovingEnd))

			v.setCarLocation(currentCarLocation)
			v.setCarAngle(currentCarAngle)
			v.hideDestinationPoint()
		}
	}

	private fun onHandleDestinationClick(location: PointLocation) {
		if (!isDriving) {
			isDriving = true
			currentDestinationLocation = location
			ifViewAttached {
				it.showDestinationPoint(currentDestinationLocation)
			}
			val fromPoint = currentCarLocation.toPoint()
			val toPoint = currentDestinationLocation.toPoint()
			val newAngle = LocationUtils.calcRotationAngleInDegrees(fromPoint, toPoint)

			val fromAngle = currentCarAngle.toInt()
			val toAngle = newAngle.toInt()

			val angleDiff = LocationUtils.distance(fromAngle, toAngle)
			val negativeDirection = LocationUtils.isNegativeDirection(fromAngle, toAngle)
			if (angleDiff > 0) {
				val angles = generateAngles(fromAngle, toAngle, negativeDirection)
				ifViewAttached { it.rotateCar(angles, toAngle) }
			}
		}
	}

	private fun generateAngles(fromAngle: Int, toAngle: Int, negativeDirection: Boolean): List<Int> {
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

	private fun onCarRotationEnd(angle: Float) {
		currentCarAngle = angle

		startMovingCar(currentCarLocation, currentDestinationLocation)
	}

	private fun startMovingCar(carLocation: CarLocation, destinationLocation: PointLocation) {
		val stepCount = CAR_DRIVE_STEP_COUNT
		val stepX = abs(destinationLocation.x - carLocation.x) / (1.0 * stepCount)
		val stepY = abs(destinationLocation.y - carLocation.y) / (1.0 * stepCount)
		val isRightDirectionX = carLocation.x < destinationLocation.x
		val isTopDirectionY = carLocation.y > destinationLocation.y

		val coordsX = IntArray(stepCount)
		val coordsY = IntArray(stepCount)

		for (step in 0 until stepCount) {
			val offsetX = (if (isRightDirectionX) stepX * step else -stepX * step)
			val offsetY = (if (isTopDirectionY) -stepY * step else stepY * step)
			coordsX[step] = (carLocation.x + offsetX).toInt()
			coordsY[step] = (carLocation.y + offsetY).toInt()
		}

		ifViewAttached {
			it.moveCar(stepCount, coordsX, coordsY, destinationLocation)
		}
	}

	private fun onCarMovingEnd(location: CarLocation) {
		ifViewAttached { it.hideDestinationPoint() }
		currentCarLocation = location
		isDriving = false
	}

	override fun destroy() {
		disposables.clear()
		super.destroy()
	}

	companion object {
		private const val CAR_DRIVE_STEP_COUNT = 100
	}
}