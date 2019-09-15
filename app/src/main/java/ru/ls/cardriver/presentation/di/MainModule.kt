package ru.ls.cardriver.presentation.di

import dagger.Module
import dagger.Provides
import ru.ls.cardriver.data.interactor.LocationInteractor
import ru.ls.cardriver.di.scope.Presentation
import ru.ls.cardriver.presentation.main.MainPresenter
import ru.ls.cardriver.rx.SchedulerProvider

@Module
class MainModule {

	@Presentation
	@Provides
	fun provideMainPresenter(
		interactor: LocationInteractor,
		schedulerProvider: SchedulerProvider
	): MainPresenter = MainPresenter(interactor, schedulerProvider)
}