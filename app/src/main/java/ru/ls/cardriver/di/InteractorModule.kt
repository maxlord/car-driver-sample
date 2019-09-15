package ru.ls.cardriver.di

import dagger.Module
import dagger.Provides
import ru.ls.cardriver.data.interactor.LocationInteractor
import ru.ls.cardriver.data.interactor.LocationInteractorImpl
import javax.inject.Singleton

@Module
class InteractorModule {

	@Provides
	@Singleton
	fun provideLocationInteractor(): LocationInteractor = LocationInteractorImpl()
}