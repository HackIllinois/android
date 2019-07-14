package org.hackillinois.android.view

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.android.synthetic.main.fragment_indoor_maps.view.*

import org.hackillinois.android.R

class IndoorMapsFragment : Fragment() {

    private var map: SubsamplingScaleImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_indoor_maps, container, false)

        map = view.map

        view.indoor_tabs.addOnTabSelectedListener(TabListener())
        map?.setMinimumTileDpi(MINIMUM_TILE_DPI)
        setImage(0)

        return view
    }

    private inner class TabListener : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            setImage(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }

    private fun setImage(index: Int) {
        map?.setImage(ImageSource.resource(imageResources[index]))
    }

    companion object {
        private val imageResources = intArrayOf(R.drawable.dcl_indoor, R.drawable.eceb_indoor, R.drawable.siebel_indoor)
        private val MINIMUM_TILE_DPI = 160
    }
}
