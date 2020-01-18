package org.hackillinois.android.view.maps.indoor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.hackillinois.android.R

class IndoorMapsFragment : Fragment() {

    private var index = 0

    private val dclIndoorFragment by lazy {
        BuildingMapFragment.newInstance(
                listOf("FLOOR 1"),
                listOf(R.drawable.dcl)
        )
    }

    private val ecebIndoorFragment by lazy {
        BuildingMapFragment.newInstance(
                listOf("FLOOR 1", "FLOOR 2", "FLOOR 3"),
                listOf(R.drawable.eceb_floor_1, R.drawable.eceb_floor_2, R.drawable.eceb_floor_3)
        )
    }

    private val kenneyIndoorFragment by lazy {
        BuildingMapFragment.newInstance(
                listOf("FLOOR 1"),
                listOf(R.drawable.kenney)
        )
    }

    private val siebelIndoorFragment by lazy {
        BuildingMapFragment.newInstance(
                listOf("BASEMENT", "FLOOR 1", "FLOOR 2"),
                listOf(R.drawable.siebel_floor_0, R.drawable.siebel_floor_1, R.drawable.siebel_floor_2)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_indoor_maps, container, false)
        changeFragment(index)
        return view
    }

    fun onTabSelected(index: Int) {
        changeFragment(index)
    }

    private fun changeFragment(index: Int) {
        this.index = index

        val fragment = when (index) {
            0 -> dclIndoorFragment
            1 -> ecebIndoorFragment
            2 -> kenneyIndoorFragment
            3 -> siebelIndoorFragment
            else -> siebelIndoorFragment
        }

        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.indoorMapsContentFrame, fragment)
            commit()
        }
    }

    companion object {
        fun newInstance() = IndoorMapsFragment()
    }
}
