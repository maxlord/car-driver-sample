package ru.ls.cardriver.presentation.main

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.animation.doOnEnd
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.jakewharton.rxbinding3.view.touches
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import ru.ls.cardriver.R
import ru.ls.cardriver.data.interactor.LocationInteractorImpl
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointItem
import ru.ls.cardriver.domain.model.PointLocation

class MainFragment : MvpFragment<MainView, MainPresenter>(), MainView {

	private val rotationEndRelay: Relay<Float> = PublishRelay.create()
	private val movingEndRelay: Relay<PointItem> = PublishRelay.create()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_main, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		container.post {
			presenter.init(container.width, container.height)
		}
	}

	override fun createPresenter(): MainPresenter = MainPresenter(LocationInteractorImpl())

	override fun destinationClicks(): Observable<PointItem> =
		container.touches { it.action == MotionEvent.ACTION_DOWN }
			.map { PointLocation(it.x.toInt(), it.y.toInt()) }

	override fun onCarRotationEnds(): Observable<Float> = rotationEndRelay

	override fun onCarMovingEnds(): Observable<PointItem> = movingEndRelay

	override fun setCarLocation(position: PointItem) {
		with(viewCar) {
			x = (position.x - measuredWidth / 2).toFloat()
			y = (position.y - measuredHeight / 2).toFloat()
			requestLayout()
		}
	}

	override fun setCarAngle(angle: Float) {
		with(viewCar) {
			rotation = angle
			requestLayout()
		}
	}

	override fun showDestinationPoint(location: PointItem) {
		with(viewDestinationPoint) {
			x = (location.x - measuredWidth / 2).toFloat()
			y = (location.y - measuredHeight / 2).toFloat()
			visibility = View.VISIBLE
			requestLayout()
		}
	}

	override fun hideDestinationPoint() {
		viewDestinationPoint.visibility = View.INVISIBLE
	}

	override fun rotateCar(angles: List<Int>, toAngle: Int) {
		ValueAnimator.ofInt(0, angles.size - 1)
			.apply {
				interpolator = AccelerateInterpolator()
				duration = CAR_ROTATION_DURATION
				addUpdateListener {
					val angle = angles[it.animatedValue as Int]
					setCarAngle(angle.toFloat())
				}
				doOnEnd {
					rotationEndRelay.accept(toAngle.toFloat())
				}
				start()
			}
	}

	override fun moveCar(
		stepCount: Int,
		coordsX: IntArray,
		coordsY: IntArray,
		destinationLocation: PointItem
	) {
		ValueAnimator.ofInt(0, stepCount - 1)
			.apply {
				interpolator = DecelerateInterpolator()
				duration = CAR_DRIVE_DURATION
				addUpdateListener {
					val step = it.animatedValue as Int
					val x = coordsX[step]
					val y = coordsY[step]
					setCarLocation(CarLocation(x, y))
				}
				doOnEnd {
					movingEndRelay.accept(CarLocation(destinationLocation.x, destinationLocation.y))
				}
				start()
			}
	}

	override fun showError(message: String) {
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}

	companion object {

		private const val CAR_ROTATION_DURATION = 1_500L
		private const val CAR_DRIVE_DURATION = 3_000L

		fun newInstance() = MainFragment()
	}
}