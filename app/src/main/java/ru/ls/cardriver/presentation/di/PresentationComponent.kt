package ru.ls.cardriver.presentation.di

import dagger.Component
import ru.ls.cardriver.di.AppComponent
import ru.ls.cardriver.di.scope.Presentation
import ru.ls.cardriver.presentation.MainActivity
import ru.ls.cardriver.presentation.main.MainFragment

@Presentation
@Component(
	modules = [
		MainActivityModule::class,
		MainModule::class
	], dependencies = [
		AppComponent::class
	]
)
interface PresentationComponent {

	fun inject(activity: MainActivity)

	fun inject(fragment: MainFragment)
}