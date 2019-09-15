package ru.ls.cardriver.rx

import io.reactivex.Scheduler

interface SchedulerProvider {

	fun computation(): Scheduler

	fun ui(): Scheduler
}