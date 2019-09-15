package ru.ls.cardriver.app

import android.app.Application
import ru.ls.cardriver.di.AppComponentHolder
import timber.log.Timber

class MainApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		initTimber()
		initDagger()
	}

	private fun initTimber() {
		Timber.plant(Timber.DebugTree())
	}

	private fun initDagger() {
		AppComponentHolder.initComponent(this)
		AppComponentHolder.appComponent.inject(this)
	}
}