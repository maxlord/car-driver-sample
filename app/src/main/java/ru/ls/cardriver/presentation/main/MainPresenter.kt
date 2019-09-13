package ru.ls.cardriver.presentation.main

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointLocation
import ru.ls.cardriver.utils.LocationUtils

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

			ifViewAttached {
				it.rotateCar(currentCarAngle, newAngle.toFloat())
			}
		}
	}

	private fun onCarRotationEnd(angle: Float) {
		currentCarAngle = angle
		ifViewAttached {
			it.moveCar(currentCarLocation, currentDestinationLocation)
		}
	}

	private fun onCarMovingEnd(location: CarLocation) {
		currentCarLocation = location
		isDriving = false
	}

	override fun destroy() {
		disposables.clear()
		super.destroy()
	}

	companion object
}