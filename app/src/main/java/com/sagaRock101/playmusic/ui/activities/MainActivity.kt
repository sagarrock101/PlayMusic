package com.sagaRock101.playmusic.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.ActivityMainBinding
import com.sagaRock101.playmusic.ui.interfaces.OnBackPressedListener
import com.sagaRock101.playmusic.utils.Utils


class MainActivity : AppCompatActivity(), OnBackPressedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefFile: String
    private lateinit var sharedPreferences: SharedPreferences
    private var navigationBarColor: Int = 0
    private val NAV_BAR_COLOR = "nav_bar_color"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        supportActionBar!!.elevation = 0f;
        setupSharedPref()
        addNavigationBarColorToSharedPref()
    }

    private fun addNavigationBarColorToSharedPref() {
        navigationBarColor = window.navigationBarColor
        var sharePrefEditor = sharedPreferences.edit()
        sharePrefEditor.putInt(NAV_BAR_COLOR, navigationBarColor)
        sharePrefEditor.apply()
    }

    private fun setupSharedPref() {
        sharedPrefFile = applicationContext.packageName
        sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp()
    }

    override fun onBackPressed() {
        window.apply {
            navigationBarColor = sharedPreferences.getInt(NAV_BAR_COLOR, R.color.colorPrimaryDark)
            statusBarColor = Utils.getColor(context, R.color.colorPrimaryDark)
        }
        super.onBackPressed()
    }

}