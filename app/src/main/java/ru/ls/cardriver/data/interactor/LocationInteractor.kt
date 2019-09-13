package ru.ls.cardriver.data.interactor

import io.reactivex.Maybe
import io.reactivex.Single
import ru.ls.cardriver.domain.model.PointItem

interface LocationInteractor {

	fun calcRotationAngleInDegrees(fromPoint: PointItem, toPoint: PointItem): Single<Double>

	fun generateAngles(fromAngle: Int, toAngle: Int): Maybe<List<Int>>
}