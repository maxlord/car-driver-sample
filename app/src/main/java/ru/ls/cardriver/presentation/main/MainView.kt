package ru.ls.cardriver.presentation.main

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import ru.ls.cardriver.domain.model.CarLocation
import ru.ls.cardriver.domain.model.PointLocation

interface MainView : MvpView {

	fun destinationClicks(): Observable<PointLocation>

	fun onCarRotationEnds(): Observable<Float>

	fun onCarMovingEnds(): Observable<CarLocation>

	fun setCarLocation(position: CarLocation)

	fun setCarAngle(angle: Float)

	fun showDestinationPoint(location: PointLocation)

	fun hideDestinationPoint()

	fun rotateCar(angles: List<Int>, toAngle: Int)

	fun moveCar(stepCount: Int, coordsX: IntArray, coordsY: IntArray, destinationLocation: PointLocation)
}