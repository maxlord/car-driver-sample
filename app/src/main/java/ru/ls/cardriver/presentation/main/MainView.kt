package ru.ls.cardriver.presentation.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.ls.cardriver.domain.model.PointItem

interface MainView : MvpView {

	fun centerChanges(): Observable<PointItem>

	fun destinationClicks(): Observable<PointItem>

	fun onCarRotationEnds(): Observable<Float>

	fun onCarMovingEnds(): Observable<PointItem>

	fun setCarLocation(position: PointItem)

	fun setCarAngle(angle: Float)

	fun showDestinationPoint(location: PointItem)

	fun hideDestinationPoint()

	fun rotateCar(angles: List<Int>, toAngle: Int)

	fun moveCar(
		stepCount: Int,
		coordsX: IntArray,
		coordsY: IntArray,
		destinationLocation: PointItem
	)

	fun showError(message: String)
}