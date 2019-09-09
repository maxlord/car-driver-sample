package ru.ls.cardriver.presentation.main

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointLocation
import ru.ls.cardriver.utils.LocationUtils
import kotlin.math.abs

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
		if (!isDriving) {
			isDriving = true
			currentDestinationLocation = location
			ifViewAttached {
				it.showDestinationPoint(currentDestinationLocation)
			}
			val fromPoint = currentCarLocation.toPoint()
			val toPoint = currentDestinationLocation.toPoint()
			val newAngle = LocationUtils.calcRotationAngleInDegrees(fromPoint, toPoint)
			rotateCar(currentCarAngle, newAngle.toFloat())
			moveCar(currentCarLocation, currentDestinationLocation)
		}
	}

	private fun rotateCar(fromAngle: Float, toAngle: Float) {
		if (abs(fromAngle - toAngle) < 0.001) return

		ValueAnimator.ofFloat(fromAngle, toAngle)
			.apply {
				interpolator = AccelerateDecelerateInterpolator()
				duration = CAR_ROTATION_DURATION
				addUpdateListener {
					val angle = it.animatedValue as Float
					ifViewAttached { it.setCarAngle(angle) }
				}
				doOnEnd {
					currentCarAngle = toAngle
				}
				start()
			}
	}

	private fun moveCar(
		carLocation: CarLocation,
		destinationLocation: PointLocation
	) {
		val stepCount = CAR_DRIVE_STEP_COUNT
		val stepX = abs(destinationLocation.x - carLocation.x) / (1.0 * stepCount)
		val stepY = abs(destinationLocation.y - carLocation.y) / (1.0 * stepCount)
		val isRightDirectionX = carLocation.x < destinationLocation.x
		val isTopDirectionY = carLocation.y > destinationLocation.y
		ValueAnimator.ofInt(0, stepCount - 1)
			.apply {
				interpolator = DecelerateInterpolator()
				duration = CAR_DRIVE_DURATION
				addUpdateListener {
					val step = it.animatedValue as Int
					val x =
						currentCarLocation.x + (if (isRightDirectionX) stepX * step else -stepX * step)
					val y =
						currentCarLocation.y + (if (isTopDirectionY) -stepY * step else stepY * step)
					ifViewAttached {
						it.setCarLocation(CarLocation(x.toFloat(), y.toFloat()))
					}
				}
				doOnEnd {
					currentCarLocation = CarLocation(destinationLocation.x, destinationLocation.y)
					isDriving = false
				}
				start()
			}
	}

	override fun destroy() {
		disposables.clear()
		super.destroy()
	}

	companion object {
		private const val CAR_ROTATION_DURATION = 1_500L
		private const val CAR_DRIVE_DURATION = 3_000L
		private const val CAR_DRIVE_STEP_COUNT = 1000
	}
}