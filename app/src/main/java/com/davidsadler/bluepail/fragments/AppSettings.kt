package com.davidsadler.bluepail.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.activities.DarkModeConfig
import com.davidsadler.bluepail.activities.MainActivity
import com.davidsadler.bluepail.adapters.OnSettingsCellClickedListener
import com.davidsadler.bluepail.adapters.OnSwitchToggledListener
import com.davidsadler.bluepail.adapters.SettingsAdapter
import com.davidsadler.bluepail.util.*
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
                view?.let {view ->
                    Snackbar.make(view,snackbarMessage,Snackbar.LENGTH_SHORT).show()
                }
                findNavController().popBackStack()
                findNavController().navigate(R.id.appSettings)
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
        activity?.lockScreenOrientation()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.fullUserScreenOrientation()
    }

    private fun setupRecyclerView() {
        this.context?.let {context ->
            recyclerView_app_settings.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = SettingsAdapter(context,this,this)
            recyclerView_app_settings.adapter = adapter
            adapter.isDarkModeEnabled = activity!!.getPreferences(Context.MODE_PRIVATE).getBoolean(SHARED_PREF_DARK_MODE_BOOL, false)
            recyclerView_app_settings.addItemDecoration(MarginItemDecorator(32, RecyclerViewLayoutType.VERTICAL_LIST))
        }
    }
}
