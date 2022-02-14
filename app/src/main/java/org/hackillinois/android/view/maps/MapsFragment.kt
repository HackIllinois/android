package org.hackillinois.android.view.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.fragment_maps.view.*
import org.hackillinois.android.R
import org.hackillinois.android.view.maps.indoor.IndoorMapsFragment

class MapsFragment : Fragment() {

    private lateinit var outdoorMapsButton: Button
    private lateinit var indoorMapsButton: Button

    private var outdoors = false

    private lateinit var contentFrame: FrameLayout

    private val outdoorMapsFragment by lazy { OutdoorMapsFragment.newInstance() }
    private val indoorMapsFragment by lazy { IndoorMapsFragment.newInstance() }

    private var index: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        outdoorMapsButton = view.findViewById(R.id.outdoorButton)
        indoorMapsButton = view.findViewById(R.id.indoorButton)
        contentFrame = view.findViewById(R.id.mapsContainer)

        outdoorMapsButton.setOnClickListener(outdoorMapsClickListener)
        indoorMapsButton.setOnClickListener(indoorMapsClickListener)
        view.tabLayout.addOnTabSelectedListener(tabListener)

        outdoorMapsButton.isSelected = false
        indoorMapsButton.isSelected = true
        indoorMapsFragment.connectTabs(view.tabLayout)
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.mapsContainer, indoorMapsFragment)
            commit()
        }

        return view
    }

    private val tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
            index = tab.position
            if (outdoors) {
                outdoorMapsFragment.onTabReselected(index)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            index = tab.position
            if (outdoors) {
                outdoorMapsFragment.onTabUnselected(index)
            }
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            index = tab.position
            if (outdoors) {
                outdoorMapsFragment.onTabSelected(index)
            } else {
                indoorMapsFragment.onTabSelected(index)
            }
        }
    }

    private val outdoorMapsClickListener = View.OnClickListener {
        outdoorMapsButton.isSelected = true
        indoorMapsButton.isSelected = false
        outdoors = true

        outdoorMapsFragment.onTabSelected(index)
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.mapsContainer, outdoorMapsFragment)
            commit()
        }
    }

    private val indoorMapsClickListener = View.OnClickListener {
        outdoorMapsButton.isSelected = false
        indoorMapsButton.isSelected = true
        outdoors = false

        indoorMapsFragment.onTabSelected(index)
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.mapsContainer, indoorMapsFragment)
            commit()
        }?.runOnCommit {
            indoorMapsFragment.connectTabs(tabLayout)
        }
    }
}
