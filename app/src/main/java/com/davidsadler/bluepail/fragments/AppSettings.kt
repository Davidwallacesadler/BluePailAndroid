package com.davidsadler.bluepail.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.OnSettingsCellClickedListener
import com.davidsadler.bluepail.adapters.OnSwitchToggledListener
import com.davidsadler.bluepail.adapters.SettingsAdapter
import com.davidsadler.bluepail.util.MarginItemDecorator
import com.davidsadler.bluepail.util.RecyclerViewLayoutType
import kotlinx.android.synthetic.main.fragment_app_settings.*

class AppSettings : Fragment(), OnSwitchToggledListener, OnSettingsCellClickedListener {

    override fun onSettingsCellClicked(position: Int) {
        println("Cell Tapped at Position: $position")
    }

    override fun onSwitchToggled(position: Int, state: Boolean) {
        println("Switch Cell at position: $position has switch state: $state")
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
            recyclerView_app_settings.adapter = SettingsAdapter(it,
                this,
                this)
            recyclerView_app_settings.addItemDecoration(MarginItemDecorator(16, RecyclerViewLayoutType.VERTICAL_LIST))
        }
    }
}
