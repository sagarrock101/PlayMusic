package com.sagaRock101.playmusic.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.ActivityMainBinding
import com.sagaRock101.playmusic.ui.fragment.ParentTabFragment


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportFragmentManager?.beginTransaction()?.replace(binding.flContainer.id, ParentTabFragment())
            .commit()

    }


}