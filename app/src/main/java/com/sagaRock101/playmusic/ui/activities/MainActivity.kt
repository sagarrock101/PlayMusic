package com.sagaRock101.playmusic.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.ActivityMainBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.ui.fragment.ParentTabFragment
import com.sagaRock101.playmusic.ui.fragment.PlayerFragment
import com.sagaRock101.playmusic.ui.interfaces.OnBackPressedListener
import com.sagaRock101.playmusic.ui.interfaces.OnSongItemClickedListener
import com.sagaRock101.playmusic.utils.Utils


class MainActivity : AppCompatActivity(), OnBackPressedListener, OnSongItemClickedListener  {
    private var motionLayoutStateFlag: Boolean = false
    private var playerFragment: PlayerFragment? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefFile: String
    private lateinit var sharedPreferences: SharedPreferences
    private var navigationBarColor: Int = 0
    private val NAV_BAR_COLOR = "nav_bar_color"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupSharedPref()
        addNavigationBarColorToSharedPref()
        addFragment(ParentTabFragment().apply {
            listener = this@MainActivity
        })
        if(savedInstanceState != null) {
            this.playerFragment = supportFragmentManager.getFragment(savedInstanceState, "player") as PlayerFragment
            motionLayoutStateFlag = savedInstanceState?.getBoolean("motion_layout_state")
        }
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerFragment?.let {
                playerFragment?.setMotionLayoutTransFlag(motionLayoutStateFlag)
            }
        } else {
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.flTabs.id, fragment)
            .commit()
    }

    private fun addPlayerFragment(song: Song, position: Int) {
        if(playerFragment == null) {
            playerFragment = PlayerFragment()
            playerFragment!!.setSongData(song, position)
            playerFragment!!.let {
                supportFragmentManager.beginTransaction()
                    .replace(binding.flContainer.id, it).commit()
            }
        } else {
            playerFragment!!.setSongData(song, position)
            playerFragment!!.resetPlayer()
            playerFragment!!.releaseVisualizer()
            var fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.detach(playerFragment!!)
            fragmentTransaction.attach(playerFragment!!)
            fragmentTransaction.commit()
        }
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

    override fun onBackPressed() {
        window.apply {
            navigationBarColor = sharedPreferences.getInt(NAV_BAR_COLOR, R.color.colorPrimaryDark)
            statusBarColor = Utils.getColor(context, R.color.colorPrimaryDark)
        }
        if(playerFragment?.getMotionLayout()?.currentState == R.id.expanded) {
            playerFragment?.makeTransitionToCollapse()
        } else
            super.onBackPressed()
    }

    override fun startPlayer(song: Song, position: Int) {
        addPlayerFragment(song, position)
        binding.clMain.transitionToStart()
        binding.clMain.transitionToEnd()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState?.run {
            supportFragmentManager.putFragment(outState, "player", playerFragment!!)
            putBoolean("motion_layout_state", checkMotionLayoutState())
        }
        super.onSaveInstanceState(outState)
    }

    private fun checkMotionLayoutState(): Boolean {
       return playerFragment?.getMotionLayout()?.currentState == R.id.expanded
    }


}