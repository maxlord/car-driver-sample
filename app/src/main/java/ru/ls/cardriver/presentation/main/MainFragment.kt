package ru.ls.cardriver.presentation.main

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main.*
import ru.ls.cardriver.R

class MainFragment : Fragment() {

	companion object {

		fun newInstance() = MainFragment()
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
		emulateDriving()
	}

	private fun emulateDriving() {
		val carView = viewCar

		carView.rotation = 45f
		val r = carView.rotation
		val animator = ValueAnimator.ofFloat(r + 0f, r + 360f)
		animator.interpolator = AccelerateDecelerateInterpolator()
		animator.duration = 2_000
		animator.addUpdateListener {
			val v = it.animatedValue as Float
			Log.d("MainFragment", "anim value = $v")
			carView.rotation = v
			carView.x += 1f
			carView.y -= 1f
			carView.requestLayout()
		}
		animator.doOnEnd { }
		animator.start()
	}
}