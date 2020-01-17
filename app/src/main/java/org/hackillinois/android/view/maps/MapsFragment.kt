package org.hackillinois.android.view.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_maps.view.*
import org.hackillinois.android.R

class MapsFragment : Fragment() {

    private lateinit var outdoorMapsButton: Button
    private lateinit var indoorMapsButton: Button

    private var outdoors = true

    private lateinit var contentFrame: FrameLayout

    private val outdoorMapsFragment = OutdoorMapsFragment.newInstance()
    private val indoorMapsFragment = IndoorMapsFragment.newInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        outdoorMapsButton = view.findViewById(R.id.outdoorButton)
        indoorMapsButton = view.findViewById(R.id.indoorButton)
        contentFrame = view.findViewById(R.id.mapsContainer)

        outdoorMapsButton.setOnClickListener(outdoorMapsClickListener)
        indoorMapsButton.setOnClickListener(indoorMapsClickListener)
        view.tabLayout.addOnTabSelectedListener(tabListener)

        outdoorMapsButton.isSelected = true
        indoorMapsButton.isSelected = false
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.mapsContainer, outdoorMapsFragment)
            commit()
        }

        return view
    }

    private val tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
            if (outdoors) {
                outdoorMapsFragment.onTabReselected(tab.position)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            if (outdoors) {
                outdoorMapsFragment.onTabUnselected(tab.position)
            }
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            if (outdoors) {
                outdoorMapsFragment.onTabSelected(tab.position)
            } else {
                indoorMapsFragment.onTabSelected(tab.position)
            }
        }
    }

    private val outdoorMapsClickListener = View.OnClickListener {
        outdoorMapsButton.isSelected = true
        indoorMapsButton.isSelected = false
        outdoors = true

        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.mapsContainer, outdoorMapsFragment)
            commit()
        }
    }

    private val indoorMapsClickListener = View.OnClickListener {
        outdoorMapsButton.isSelected = false
        indoorMapsButton.isSelected = true
        outdoors = false

        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.mapsContainer, indoorMapsFragment)
            commit()
        }
    }
}