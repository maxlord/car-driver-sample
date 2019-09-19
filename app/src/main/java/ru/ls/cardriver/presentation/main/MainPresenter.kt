package ru.ls.cardriver.presentation.main

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import ru.ls.cardriver.data.interactor.LocationInteractor
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointItem
import ru.ls.cardriver.domain.model.PointLocation
import ru.ls.cardriver.rx.SchedulerProvider

class MainPresenter(
	private val interactor: LocationInteractor,
	private val schedulerProvider: SchedulerProvider
) : MvpBasePresenter<MainView>() {

	private val disposables = CompositeDisposable()
	private var currentCarLocation: PointItem = CarLocation(0, 0)
	private var currentDestinationLocation: PointItem = PointLocation(0, 0)
	private var currentCarAngle: Float = 0f
	private var isDriving = false

	fun init() {
		ifViewAttached { v ->
			disposables.add(v.centerChanges().subscribe(this::onHandleCenterChange))
			disposables.add(v.destinationClicks().subscribe(this::onHandleDestinationClick))
			disposables.add(v.onCarRotationEnds().subscribe(this::onCarRotationEnd))
			disposables.add(v.onCarMovingEnds().subscribe(this::onCarMovingEnd))

			v.setCarAngle(currentCarAngle)
			v.hideDestinationPoint()
		}
	}

	private fun onHandleCenterChange(location: PointItem) {
		currentCarLocation = CarLocation(location.x, location.y)
		ifViewAttached { v -> v.setCarLocation(currentCarLocation) }
	}

	private fun onHandleDestinationClick(location: PointItem) {
		if (!isDriving) {
			isDriving = true
			currentDestinationLocation = location
			ifViewAttached {
				it.showDestinationPoint(currentDestinationLocation)
			}
			val fromPoint = currentCarLocation
			val toPoint = currentDestinationLocation
			disposables.add(
				interactor.calcRotationAngleInDegrees(fromPoint, toPoint)
					.flatMapMaybe { angle ->
						val fromAngle = currentCarAngle.toInt()
						val toAngle = angle.toInt()
						interactor.generateAngles(fromAngle, toAngle)
							.map { it to toAngle }
					}
					.subscribeOn(schedulerProvider.computation())
					.observeOn(schedulerProvider.ui())
					.subscribe({ (angles, toAngle) ->
						ifViewAttached { it.rotateCar(angles, toAngle) }
					}, { error ->
						ifViewAttached { it.showError(error.localizedMessage) }
					})
			)
		}
	}

	private fun onCarRotationEnd(angle: Float) {
		currentCarAngle = angle
		currentCarLocation?.let {
			startMovingCar(it, currentDestinationLocation)
		}
	}

	private fun startMovingCar(carLocation: PointItem, destinationLocation: PointItem) {
		val stepCount = CAR_DRIVE_STEP_COUNT
		disposables.add(
			interactor.generateCoordsForRoute(carLocation, destinationLocation, stepCount)
				.observeOn(schedulerProvider.ui())
				.subscribe({ (coordsX, coordsY) ->
					ifViewAttached {
						it.moveCar(stepCount, coordsX, coordsY, destinationLocation)
					}
				}, { error ->
					ifViewAttached { it.showError(error.localizedMessage) }
				})
		)
	}

	private fun onCarMovingEnd(location: PointItem) {
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