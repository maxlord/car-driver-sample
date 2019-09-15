package ru.ls.cardriver.di

import dagger.Component
import ru.ls.cardriver.app.MainApplication
import ru.ls.cardriver.data.interactor.LocationInteractor
import ru.ls.cardriver.rx.SchedulerProvider
import javax.inject.Singleton

@Singleton
@Component(
	modules = [
		AppModule::class,
		InteractorModule::class,
		RxModule::class
	]
)
interface AppComponent {

	fun inject(application: MainApplication)

	fun schedulerProvider(): SchedulerProvider

	fun locationInteractor(): LocationInteractor
}