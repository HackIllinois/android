package org.hackillinois.android.view.maps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.android.synthetic.main.fragment_indoor_maps.view.*

import org.hackillinois.android.R

class IndoorMapsFragment : Fragment() {

    private var map: SubsamplingScaleImageView? = null
    private var index = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_indoor_maps, container, false)

        map = view.map
        map?.setMinimumTileDpi(MINIMUM_TILE_DPI)
        setImage(index)

        return view
    }

    fun onTabSelected(index: Int) {
        setImage(index)
    }

    private fun setImage(index: Int) {
        this.index = index
        map?.setImage(ImageSource.resource(imageResources[index]))
    }

    companion object {
        // TODO: get new indoor maps
        private val imageResources = intArrayOf(R.drawable.dcl_indoor, R.drawable.eceb_indoor, R.drawable.siebel_indoor, R.drawable.dcl_indoor)
        private val MINIMUM_TILE_DPI = 160
        fun newInstance() = IndoorMapsFragment()
    }
}
