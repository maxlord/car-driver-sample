package ru.ls.cardriver.presentation.main

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.ls.cardriver.data.interactor.LocationInteractor
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointItem
import ru.ls.cardriver.domain.model.PointLocation

class MainPresenter(
	private val interactor: LocationInteractor
) : MvpBasePresenter<MainView>() {

	private val disposables = CompositeDisposable()
	private var containerWidth: Int = 0
	private var containerHeight: Int = 0
	private var currentCarLocation: PointItem = CarLocation(containerWidth, containerHeight)
	private var currentDestinationLocation: PointItem = PointLocation(0, 0)
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
					.subscribeOn(Schedulers.computation())
					.observeOn(AndroidSchedulers.mainThread())
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
		startMovingCar(currentCarLocation, currentDestinationLocation)
	}

	private fun startMovingCar(carLocation: PointItem, destinationLocation: PointItem) {
		val stepCount = CAR_DRIVE_STEP_COUNT
		disposables.add(
			interactor.generateCoordsForRoute(carLocation, destinationLocation, stepCount)
				.observeOn(AndroidSchedulers.mainThread())
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