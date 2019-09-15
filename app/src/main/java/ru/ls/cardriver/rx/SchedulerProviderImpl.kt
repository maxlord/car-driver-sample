package ru.ls.cardriver.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerProviderImpl : SchedulerProvider {

	override fun computation(): Scheduler = Schedulers.computation()

	override fun ui(): Scheduler = AndroidSchedulers.mainThread()
}