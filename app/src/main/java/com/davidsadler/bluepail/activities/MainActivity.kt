package com.davidsadler.bluepail.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.fragments.PlantListDirections
import com.davidsadler.bluepail.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkDarkModeStatus()
        setSupportActionBar(toolbar)
        initializeNavController()
        setupActionBar(navController)
        setupFab()
        checkOnboardingStatus()
        NotificationHelper.createNotificationChannel(this)
    }

    override fun onResume() {
        super.onResume()
        setupOnNavigationDestinationChangedListener()
        initializeNavController()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBundle("nav_state", navHostFragment.findNavController().saveState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navHostFragment.findNavController().restoreState(savedInstanceState.getBundle("nav_state"))
    }

    private fun setupActionBar(navController: NavController) {
        NavigationUI.setupActionBarWithNavController(this,navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.title) {
            getString(R.string.menu_item_settings) -> {
                val action = PlantListDirections.actionPlantListToAppSettings()
                navController.navigate(action)
                true
            }
            else -> {
                val navigated = NavigationUI.onNavDestinationSelected(item,navController)
                navigated || super.onOptionsItemSelected(item)
            }
        }
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
            val action = PlantListDirections.actionPlantListToPlantDetail()
            navController.navigate(action)
        }
    }

    private fun setupOnNavigationDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.plantList -> {
                    showFabAndActionBarItems()
                }
                R.id.appSettings -> {
                    hideFabAndActionBarItems()
                }
                R.id.plantDetail -> {
                    hideFabAndActionBarItems()
                }
                R.id.onboarding -> {
                    hideFabAndActionBar()
                }
            }
        }
    }

    fun shouldEnableDarkMode(darkModeConfig: DarkModeConfig) {
        when(darkModeConfig){
            DarkModeConfig.YES -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            DarkModeConfig.NO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            DarkModeConfig.FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun checkDarkModeStatus() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPref.getBoolean(SHARED_PREF_DARK_MODE_BOOL,false)
        if (isDarkModeEnabled) {
            shouldEnableDarkMode(DarkModeConfig.YES)
        }
    }

    private fun checkOnboardingStatus() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val onboardingPresented = sharedPref.getBoolean(SHARED_PREF_ONBOARDING_BOOL, false)
        if (!onboardingPresented) {
            navController.navigate(R.id.onboarding)
        }
    }

    private fun hideFabAndActionBarItems() {
        fab_create_plant.hide()
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

    private fun hideFabAndActionBar() {
        fab_create_plant.hide()
        toolbar.isVisible = false
    }

    private fun showFabAndActionBarItems() {
        fab_create_plant.show()
        if (!toolbar.isVisible) {
            toolbar.isVisible = true
        }
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
}

enum class DarkModeConfig {
    YES,
    NO
    //FOLLOW_SYSTEM
}
