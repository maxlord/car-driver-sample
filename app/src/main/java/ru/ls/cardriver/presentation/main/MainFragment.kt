package ru.ls.cardriver.presentation.main

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.jakewharton.rxbinding3.view.touches
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import ru.ls.cardriver.R
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointLocation
import timber.log.Timber

class MainFragment : MvpFragment<MainView, MainPresenter>(), MainView {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_main, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.init(container.measuredWidth.toFloat(), container.measuredHeight.toFloat())
	}

	override fun createPresenter(): MainPresenter = MainPresenter()

	override fun destinationClicks(): Observable<PointLocation> =
		container.touches { it.action == MotionEvent.ACTION_DOWN }
			.map { PointLocation(it.x, it.y) }

	override fun setCarLocation(position: CarLocation) {
		with(viewCar) {
			x = position.x
			y = position.y
			requestLayout()
		}
	}

	override fun setCarAngle(angle: Float) {
		with(viewCar) {
			rotation = angle
			requestLayout()
		}
		view?.let {
			Snackbar.make(it, "Current Angle = $angle", Snackbar.LENGTH_INDEFINITE).show()
		}
	}

	override fun showDestinationPoint(location: PointLocation) {
		with(viewDestinationPoint) {
			x = location.x - measuredWidth / 2
			y = location.y - measuredHeight / 2
			visibility = View.VISIBLE
			requestLayout()
		}
	}

	override fun hideDestinationPoint() {
		viewDestinationPoint.visibility = View.INVISIBLE
	}

	@Deprecated("")
	private fun emulateDriving() {
		val carView = viewCar

		carView.rotation = 45f
		val r = carView.rotation
		val animator = ValueAnimator.ofFloat(r + 0f, r + 360f)
		animator.interpolator = AccelerateDecelerateInterpolator()
		animator.duration = 2_000
		animator.addUpdateListener {
			val v = it.animatedValue as Float
			Timber.d("anim value = $v")
			carView.rotation = v
			carView.x += 1f
			carView.y -= 1f
			carView.requestLayout()
		}
		animator.doOnEnd { }
		animator.start()
	}

	companion object {

		fun newInstance() = MainFragment()
	}
}