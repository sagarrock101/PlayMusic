package com.sagaRock101.playmusic.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.ActivityMainBinding
import com.sagaRock101.playmusic.ui.interfaces.OnBackPressedListener


class MainActivity : AppCompatActivity(), OnBackPressedListener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar!!.elevation = 0f;
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}