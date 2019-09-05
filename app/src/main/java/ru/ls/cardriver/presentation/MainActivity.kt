package ru.ls.cardriver.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.ls.cardriver.R
import ru.ls.cardriver.presentation.main.MainFragment

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		var fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT)
		if (fragment == null) {
			fragment = MainFragment.newInstance()
			supportFragmentManager
				.beginTransaction()
				.replace(R.id.container, fragment, TAG_FRAGMENT)
				.commit()
		}
	}

	companion object {

		const val TAG_FRAGMENT = "main_fragment"
	}
}