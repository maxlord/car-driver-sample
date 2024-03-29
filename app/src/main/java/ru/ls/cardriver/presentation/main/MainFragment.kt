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
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointItem
import ru.ls.cardriver.domain.model.PointLocation
import ru.ls.cardriver.presentation.MainActivity
import javax.inject.Inject

class MainFragment : MvpFragment<MainView, MainPresenter>(), MainView {

	private val rotationEndRelay: Relay<Float> = PublishRelay.create()
	private val movingEndRelay: Relay<PointItem> = PublishRelay.create()
	private val centerChangesRelay: Relay<PointItem> = PublishRelay.create()

	private var rotationAnimator: ValueAnimator? = null
	private var movingAnimator: ValueAnimator? = null

	@Inject
	lateinit var _presenter: MainPresenter

	override fun onCreate(savedInstanceState: Bundle?) {
		(requireActivity() as MainActivity).component.inject(this)
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_main, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.init()
		container.post {
			centerChangesRelay.accept(object : PointItem {
				override val x: Int = container.width / 2
				override val y: Int = container.height / 2
			})
		}
	}

	override fun onStop() {
		rotationAnimator?.cancel()
		movingAnimator?.cancel()
		super.onStop()
	}

	override fun createPresenter(): MainPresenter = _presenter

	override fun centerChanges(): Observable<PointItem> = centerChangesRelay

	override fun destinationClicks(): Observable<PointItem> =
		container.touches { it.action == MotionEvent.ACTION_DOWN }
			.map { PointLocation(it.x.toInt(), it.y.toInt()) }

	override fun onCarRotationEnds(): Observable<Float> = rotationEndRelay

	override fun onCarMovingEnds(): Observable<PointItem> = movingEndRelay

	override fun setCarLocation(position: PointItem) {
		with(viewCar) {
			x = (position.x - measuredWidth / 2).toFloat()
			y = (position.y - measuredHeight / 2).toFloat()
			invalidate()
		}
	}

	override fun setCarAngle(angle: Float) {
		with(viewCar) {
			rotation = angle
			invalidate()
		}
	}

	override fun showDestinationPoint(location: PointItem) {
		with(viewDestinationPoint) {
			x = (location.x - measuredWidth / 2).toFloat()
			y = (location.y - measuredHeight / 2).toFloat()
			visibility = View.VISIBLE
			invalidate()
		}
	}

	override fun hideDestinationPoint() {
		viewDestinationPoint.visibility = View.INVISIBLE
	}

	override fun rotateCar(angles: List<Int>, toAngle: Int) {
		rotationAnimator = ValueAnimator.ofInt(0, angles.size - 1)
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
			}
		rotationAnimator?.start()
	}

	override fun moveCar(
		stepCount: Int,
		coordsX: IntArray,
		coordsY: IntArray,
		destinationLocation: PointItem
	) {
		movingAnimator = ValueAnimator.ofInt(0, stepCount - 1)
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
			}
		movingAnimator?.start()
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