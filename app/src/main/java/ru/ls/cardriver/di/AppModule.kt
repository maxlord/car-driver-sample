package ru.ls.cardriver.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ls.cardriver.app.MainApplication
import javax.inject.Singleton

@Module
class AppModule(private val application: MainApplication) {

	@Singleton
	@Provides
	fun provideApplicationContext(): Context = application
}