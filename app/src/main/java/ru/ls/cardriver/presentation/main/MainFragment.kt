package ru.ls.cardriver.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.jakewharton.rxbinding3.view.touches
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import ru.ls.cardriver.R
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointLocation

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
		container.post {
			presenter.init(container.width.toFloat(), container.height.toFloat())
		}
	}

	override fun createPresenter(): MainPresenter = MainPresenter()

	override fun destinationClicks(): Observable<PointLocation> =
		container.touches { it.action == MotionEvent.ACTION_DOWN }
			.map { PointLocation(it.x, it.y) }

	override fun setCarLocation(position: CarLocation) {
		with(viewCar) {
			x = position.x - measuredWidth / 2
			y = position.y - measuredHeight / 2
			requestLayout()
		}
	}

	override fun setCarAngle(angle: Float) {
		with(viewCar) {
			rotation = angle
			requestLayout()
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

	companion object {

		fun newInstance() = MainFragment()
	}
}