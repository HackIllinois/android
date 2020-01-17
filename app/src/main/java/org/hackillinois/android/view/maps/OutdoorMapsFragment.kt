package org.hackillinois.android.view.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.hackillinois.android.R
import org.hackillinois.android.common.DirectionsOnClickListener

class OutdoorMapsFragment : Fragment(), OnMapReadyCallback {
    private var index: Int = 0

    private var map: GoogleMap? = null
    private var directions: FloatingActionButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outdoor_maps, container, false)

        directions = view.findViewById(R.id.directions)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.uiSettings.isIndoorLevelPickerEnabled = false
        map.addMarker(MarkerOptions().position(BUILDING_LOCATIONS[index]))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM.toFloat()))
    }

    fun onTabSelected(index: Int) {
        map?.addMarker(MarkerOptions().position(BUILDING_LOCATIONS[index]))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM.toFloat()))

        directions?.setOnClickListener(DirectionsOnClickListener(BUILDING_LOCATIONS[index], BUILDING_NAMES[index]))
    }

    fun onTabUnselected(index: Int) {
        map?.clear()
    }

    fun onTabReselected(index: Int) {
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM.toFloat()))
    }

    companion object {
        private val ZOOM = 18

        private val BUILDING_NAMES = arrayOf(
            "Electrical and Computer Engineering Building",
            "Thomas M. Siebel Center for Computer Science",
            "Digital Computing Laboratory",
            "Kenney Gym"
        )
        private val BUILDING_LOCATIONS = arrayOf(
            LatLng(40.1149213, -88.2280232),
            LatLng(40.1138194, -88.2250361),
            LatLng(40.1131428, -88.2265095),
            LatLng(40.1130590, -88.2276450)
        )

        fun newInstance() = OutdoorMapsFragment()
    }
}
