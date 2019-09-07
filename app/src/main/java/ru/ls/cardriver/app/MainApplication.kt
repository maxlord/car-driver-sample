package ru.ls.cardriver.app

import android.app.Application
import timber.log.Timber

class MainApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		initTimber()
	}

	private fun initTimber() {
		Timber.plant(Timber.DebugTree())
	}
}