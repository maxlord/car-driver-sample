package ru.ls.cardriver.di

import ru.ls.cardriver.app.MainApplication

object AppComponentHolder {

	lateinit var appComponent: AppComponent
		private set

	fun initComponent(application: MainApplication) {
		appComponent = DaggerAppComponent.builder()
			.appModule(AppModule(application))
			.build()
	}
}