package org.hackillinois.android.view.maps.indoor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import org.hackillinois.android.R

class BuildingMapFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_building_map, container, false)

        context?.let {
            val floorNames: List<String> = arguments?.getStringArray(FLOOR_NAMES_KEY)?.asList()
                ?: listOf()
            val floorImageResources: List<Int> = arguments?.getIntArray(FLOOR_IMAGES_KEY)?.asList()
                ?: listOf()

            val namesAndImages = floorNames.zip(floorImageResources)
            val adapter = BuildingMapAdapter(it, namesAndImages)

            val listView = view.findViewById<ListView>(R.id.map_list)
            listView.adapter = adapter
        }

        return view
    }

    companion object {
        private val FLOOR_NAMES_KEY = "floor_names"
        private val FLOOR_IMAGES_KEY = "floor_images"

        fun newInstance(floorNames: List<String>, floorImageResources: List<Int>): Fragment {
            val bundle = Bundle().apply {
                putStringArray(FLOOR_NAMES_KEY, floorNames.toTypedArray())
                putIntArray(FLOOR_IMAGES_KEY, floorImageResources.toIntArray())
            }
            val frag = BuildingMapFragment()
            frag.arguments = bundle
            return frag
        }
    }
}
