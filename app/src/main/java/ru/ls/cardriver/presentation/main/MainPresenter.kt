package ru.ls.cardriver.presentation.main

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import ru.ls.cardriver.domain.model.CarLocation
import timber.log.Timber

class MainPresenter : MvpBasePresenter<MainView>() {

	private val disposables = CompositeDisposable()
	private var containerWidth: Float = 0f
	private var containerHeight: Float = 0f
	private var currentCarLocation: CarLocation = CarLocation(containerWidth, containerHeight)
	private var currentCarAngle: Float = 0f

	fun init(width: Float, height: Float) {
		containerWidth = width
		containerHeight = height
		currentCarLocation = CarLocation(containerWidth / 2, containerHeight / 2)

		ifViewAttached { v ->
			disposables.add(v.destinationClicks()
				.subscribe { location ->
					Timber.d("location = $location")
					ifViewAttached {
						it.showDestinationPoint(location)
					}
				}
			)

			v.setCarLocation(currentCarLocation)
			v.setCarAngle(currentCarAngle)
			v.hideDestinationPoint()
		}
	}

	companion object {

	}
}