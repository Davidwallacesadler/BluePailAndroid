package com.davidsadler.bluepail.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.util.NOTIFICATION_CHANNEL_DESCRIPTION
import com.davidsadler.bluepail.util.NOTIFICATION_CHANNEL_ID
import com.davidsadler.bluepail.util.NOTIFICATION_CHANNEL_NAME
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initializeNavController()
        setupActionBar(navController)
        setupFab()
        createNotificationChannel()
    }

    override fun onResume() {
        super.onResume()
        setupOnNavigationDestinationChangedListener()
    }

    private fun setupActionBar(navController: NavController) {
        NavigationUI.setupActionBarWithNavController(this,navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navigated = NavigationUI.onNavDestinationSelected(item,navController)
        return navigated || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(Navigation.findNavController(this,R.id.navHostFragment),null)
    }

    private fun initializeNavController() {
        val navController = Navigation.findNavController(this,R.id.navHostFragment)
        this.navController = navController
    }

    private fun setupFab() {
        fab_create_plant.setOnClickListener {
            navController.navigate(R.id.plantDetail)
        }
    }

    private fun setupOnNavigationDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.plantList -> {
                    fab_create_plant.isVisible = true
                    toolbar.menu.findItem(R.id.plantListFilterDialog).let {
                        if (it != null) {
                            it.isVisible = true
                        }

                    }
                    toolbar.menu.findItem(R.id.appSettings).let {
                        if (it != null) {
                            it.isVisible = true
                        }
                    }
                }
                R.id.plantDetail -> {
                    fab_create_plant.isVisible = false
                    toolbar.menu.findItem(R.id.plantListFilterDialog).let {
                        if (it != null) {
                            it.isVisible = false
                        }

                    }
                    toolbar.menu.findItem(R.id.appSettings).let {
                        if (it != null) {
                            it.isVisible = false
                        }
                    }
                }
                R.id.appSettings -> {
                    fab_create_plant.isVisible = false
                    toolbar.menu.findItem(R.id.plantListFilterDialog).let {
                        if (it != null) {
                            it.isVisible = false
                        }

                    }
                    toolbar.menu.findItem(R.id.appSettings).let {
                        if (it != null) {
                            it.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NOTIFICATION_CHANNEL_NAME
            val descriptionText = NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
