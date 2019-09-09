package ru.ls.cardriver.presentation.main

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointLocation
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.atan2


class MainPresenter : MvpBasePresenter<MainView>() {

	private val disposables = CompositeDisposable()
	private var containerWidth: Float = 0f
	private var containerHeight: Float = 0f
	private var currentCarLocation: CarLocation = CarLocation(containerWidth, containerHeight)
	private var currentDestinationLocation: PointLocation = PointLocation(0f, 0f)
	private var currentCarAngle: Float = 0f
	private var isDriving = false

	fun init(width: Float, height: Float) {
		containerWidth = width
		containerHeight = height
		currentCarLocation = CarLocation(containerWidth / 2, containerHeight / 2)

		ifViewAttached { v ->
			disposables.add(v.destinationClicks().subscribe(this::onHandleDestinationClick))

			v.setCarLocation(currentCarLocation)
			v.setCarAngle(currentCarAngle)
			v.hideDestinationPoint()
		}
	}

	private fun onHandleDestinationClick(location: PointLocation) {
		Timber.d("location = $location")
		if (!isDriving) {
			currentDestinationLocation = location
			ifViewAttached {
				it.showDestinationPoint(currentDestinationLocation)
			}
			val newAngle = calculateAngle(currentCarLocation, currentDestinationLocation)
			rotateCar(currentCarAngle, newAngle)
		}
	}

	private fun rotateCar(fromAngle: Float, toAngle: Float) {
		Timber.d("from = $fromAngle; to = $toAngle")
		if (abs(fromAngle - toAngle) < 0.001) return

		val animator = ValueAnimator.ofFloat(fromAngle, toAngle)
		animator.interpolator = AccelerateDecelerateInterpolator()
		animator.duration = CAR_ROTATION_DURATION
		animator.addUpdateListener {
			val angle = it.animatedValue as Float
//			Timber.d("angle = $angle")
			ifViewAttached { it.setCarAngle(angle) }
		}
		animator.doOnEnd {
			currentCarAngle = toAngle
		}
		animator.start()
	}

	private fun calculateAngle(carLocation: CarLocation, pointLocation: PointLocation): Float {
		val angle = Math.toDegrees(
			atan2(
				(pointLocation.y - carLocation.y).toDouble(),
				(pointLocation.x - carLocation.x).toDouble()
			)
		).toFloat()

//		if (angle < 0) {
//			angle += 360f
//		}

		return angle
	}

	companion object {
		private const val CAR_ROTATION_DURATION = 1_000L
	}
}