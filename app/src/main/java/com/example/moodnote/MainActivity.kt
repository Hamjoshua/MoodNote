package com.example.moodnote

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moodnote.databinding.ActivityMainBinding
import com.example.moodnote.utils.AlarmHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var alarmHelper: AlarmHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        var appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment,
                R.id.noteFormFragment,
                R.id.statisticsFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navigation.setupWithNavController(navController)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()

        alarmHelper.cancelAlarm();
        alarmHelper.scheduleNotification(this.applicationContext)
//        alarmHelper.showNotificationNow(this.applicationContext);
//        alarmHelper.showNotificationNow();
    }


}