package com.davidsadler.bluepail.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.activities.DarkModeConfig
import com.davidsadler.bluepail.activities.MainActivity
import com.davidsadler.bluepail.adapters.OnSettingsCellClickedListener
import com.davidsadler.bluepail.adapters.OnSwitchToggledListener
import com.davidsadler.bluepail.adapters.SettingsAdapter
import com.davidsadler.bluepail.util.MarginItemDecorator
import com.davidsadler.bluepail.util.RecyclerViewLayoutType
import com.davidsadler.bluepail.util.SHARED_PREF_DARK_MODE_BOOL
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_app_settings.*

class AppSettings : Fragment(), OnSwitchToggledListener, OnSettingsCellClickedListener {

    private lateinit var adapter: SettingsAdapter

    override fun onSettingsCellClicked(position: Int) {
        when (position) {
            1 -> {
                println("Rate and review cell tapped")
            }
            2 -> {
                println("share cell tapped")
            }
        }
    }

    override fun onSwitchToggled(position: Int, state: Boolean) {
        println("Switch Cell at position: $position has switch state: $state")
        when (position) {
            0 -> {
                val mainActivity = this.activity!! as MainActivity
                var snackbarMessage = "Dark mode "
                snackbarMessage += if (state) {
                    mainActivity.shouldEnableDarkMode(DarkModeConfig.YES)
                    "enabled"
                } else {
                    mainActivity.shouldEnableDarkMode(DarkModeConfig.NO)
                    "disabled"
                }
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
                with (sharedPref.edit()) {
                    putBoolean(SHARED_PREF_DARK_MODE_BOOL, state)
                    commit()
                }
                Snackbar.make(this.view!!,snackbarMessage,Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
    }

    private fun setupRecyclerView() {
        this.context?.let {
            recyclerView_app_settings.layoutManager = LinearLayoutManager(it ,
                RecyclerView.VERTICAL,
                false)
            adapter = SettingsAdapter(it,this,this)
            recyclerView_app_settings.adapter = adapter
            adapter.isDarkModeEnabled = activity!!.getPreferences(Context.MODE_PRIVATE).getBoolean(
                SHARED_PREF_DARK_MODE_BOOL, false)
            recyclerView_app_settings.addItemDecoration(MarginItemDecorator(32, RecyclerViewLayoutType.VERTICAL_LIST))
        }
    }
}
