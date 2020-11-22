package com.sagaRock101.playmusic.ui.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.sagaRock101.playmusic.MyApplication
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.ActivityMainBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.ui.fragment.ParentTabFragment
import com.sagaRock101.playmusic.ui.fragment.PlayerFragment
import com.sagaRock101.playmusic.interfaces.OnBackPressedListener
import com.sagaRock101.playmusic.interfaces.OnSongItemClickedListener
import com.sagaRock101.playmusic.interfaces.PlayerControlsListener
import com.sagaRock101.playmusic.ui.viewModel.PlayerViewModel
import com.sagaRock101.playmusic.utils.CoroutineViewModel
import com.sagaRock101.playmusic.utils.Utils
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnBackPressedListener, OnSongItemClickedListener,
    PlayerControlsListener {

    private val PRIMARY_CHANNEL_ID = this.packageName
    private lateinit var notifyManager: NotificationManager
    private var motionLayoutStateFlag: Boolean = false
    private var playerFragment: PlayerFragment? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefFile: String
    private lateinit var sharedPreferences: SharedPreferences
    private var navigationBarColor: Int = 0
    private val NAV_BAR_COLOR = "nav_bar_color"
    private lateinit var mediaString: MediaSessionCompat

    @Inject
    lateinit var playerViewModel: PlayerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupSharedPref()
        addNavigationBarColorToSharedPref()
        addFragment(ParentTabFragment().apply {
            listener = this@MainActivity
        })
        if (savedInstanceState != null) {
            this.playerFragment =
                supportFragmentManager.getFragment(savedInstanceState, "player") as PlayerFragment
            motionLayoutStateFlag = savedInstanceState?.getBoolean("motion_layout_state")
        }
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerFragment?.let {
                playerFragment?.setMotionLayoutTransFlag(motionLayoutStateFlag)
            }
        } else {
            playerFragment?.let {
                playerFragment?.setMotionLayoutTransFlag(motionLayoutStateFlag)
            }
        }
        createNotificationChannel()

        playerViewModel.currentLD.observe(this, Observer{ mediaItemData ->
            playerFragment?.setUiFromMetaData(mediaItemData)
//            if(seekBar.max == 100) {
//                seekBar.max = mediaItemData.duration
//            }
//            var bitmap = generateBitmap(mediaItemData)
//            if (bitmap != null)
//                createPalette(bitmap)
//            setAlbumArtColor()
//            setLayoutBackgroundColor()
        })
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.flTabs.id, fragment)
            .commit()
    }

    private fun addPlayerFragment(song: Song, position: Int) {
        if (playerFragment == null) {
            playerFragment = PlayerFragment()
            playerFragment!!.setSongData(song, position)
            playerFragment!!.setPlayerControlListener(this)
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
        if (playerFragment?.getMotionLayout()?.currentState == R.id.expanded) {
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

    override fun onPlay() {

    }

    override fun onPlayerStop() {

    }

    override fun onPlayerForward() {

    }

    override fun onPlayerPrevious() {

    }

    fun createNotificationChannel() {
        notifyManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "SlidMusicPlayer", NotificationManager.IMPORTANCE_HIGH
            )
            notifyManager.createNotificationChannel(notificationChannel)
        }
    }

//    fun getNotificationBuilder(): NotificationCompat.Builder {
//        var intent = Intent("${this.packageName}")
//        var pendingIntent = PendingIntent.getBroadcast(this, 100, intent, 0)
//
//        var notification = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
//            .apply {
//                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                setSmallIcon(R.drawable.music_placeholder)
//                    .addAction(R.drawable.ic_play, "play/pause", pendingIntent)
//                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(playerFragment.getSessionToken()))
//            }
//
//
//    }
}