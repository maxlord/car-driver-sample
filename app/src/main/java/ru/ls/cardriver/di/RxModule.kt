package ru.ls.cardriver.di

import dagger.Module
import dagger.Provides
import ru.ls.cardriver.rx.SchedulerProvider
import ru.ls.cardriver.rx.SchedulerProviderImpl
import javax.inject.Singleton

@Module
class RxModule {

	@Provides
	@Singleton
	fun provideSchedulerProvider(): SchedulerProvider = SchedulerProviderImpl()
}