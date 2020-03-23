package com.davidsadler.bluepail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.davidsadler.bluepail.R
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
}
