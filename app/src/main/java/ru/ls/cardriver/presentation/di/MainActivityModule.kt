package ru.ls.cardriver.presentation.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import ru.ls.cardriver.di.scope.Presentation

@Module
class MainActivityModule(
	private val activity: AppCompatActivity
) {

	@Presentation
	@Provides
	fun provideActivity(): AppCompatActivity = activity
}