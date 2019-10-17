package org.hackillinois.android.view

import android.content.Intent
import android.location.Location
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.listener.LocationListener
import org.hackillinois.android.R
import org.hackillinois.android.common.DirectionsOnClickListener

class OutdoorMapsFragment : Fragment(), OnMapReadyCallback, LocationListener {
    private var index: Int = 0

    private var map: GoogleMap? = null
    private var directions: FloatingActionButton? = null
    private var building: TextView? = null
    private var locationInfo: LinearLayout? = null
    private var time: TextView? = null
    private var distance: TextView? = null

    private val rationalMessage = "Location permissions required to show walk times."
    private val gpsMessage = "GPS provider required to show walk times."

    private lateinit var locationManager: LocationManager

    private var userLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            locationManager = LocationManager.Builder(it.applicationContext)
                    .configuration(getLocationConfiguration())
                    .fragment(this)
                    .notify(this)
                    .build()
        }
    }

    private fun getLocationConfiguration() = LocationConfiguration.Builder()
            .keepTracking(true)
            .askForPermission(PermissionConfiguration.Builder()
                    .rationaleMessage(rationalMessage)
                    .build())
            .useGooglePlayServices(GooglePlayServicesConfiguration.Builder().build())
            .useDefaultProviders(DefaultProviderConfiguration.Builder()
                    .gpsMessage(gpsMessage)
                    .build())
            .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outdoor_maps, container, false)

        val tabs = view.findViewById<TabLayout>(R.id.tabs)
        tabs.addOnTabSelectedListener(TabListener())
        index = 0

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        directions = view.findViewById(R.id.directions)
        directions?.setOnClickListener(DirectionsOnClickListener(BUILDING_LOCATIONS[index], BUILDING_NAMES[index]))

        building = view.findViewById(R.id.building)
        building?.text = BUILDING_NAMES[index]

        locationInfo = view.findViewById(R.id.locationInfo)
        time = view.findViewById(R.id.time)
        distance = view.findViewById(R.id.distance)
        locationManager.get()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        locationManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        locationManager.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        locationManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(location: Location?) {
        userLocation = location
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.uiSettings.isIndoorLevelPickerEnabled = false
        map.addMarker(MarkerOptions().position(BUILDING_LOCATIONS[index]))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM.toFloat()))
    }

    private fun updateUserLocationView() = userLocation?.let {
        val dlat = Math.abs(BUILDING_LOCATIONS[index].latitude - it.latitude)
        val dlon = Math.abs(BUILDING_LOCATIONS[index].longitude - it.longitude)

        val EARTH_RADIUS = 3959
        val miles = 2.0 * Math.PI * EARTH_RADIUS.toDouble() * (dlat + dlon) / 360.0
        distance?.text = String.format("%.1f miles", miles)

        val WALKING_MPH = 3
        val MINUTES_IN_HOUR = 60
        val minutes = (miles / WALKING_MPH * MINUTES_IN_HOUR).toInt()
        time?.text = String.format("%d min", minutes)
        locationInfo?.visibility = View.VISIBLE
    }

    private inner class TabListener : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            index = tab.position

            map?.addMarker(MarkerOptions().position(BUILDING_LOCATIONS[index]))
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM.toFloat()))

            directions?.setOnClickListener(DirectionsOnClickListener(BUILDING_LOCATIONS[index], BUILDING_NAMES[index]))

            building?.text = BUILDING_NAMES[index]
            updateUserLocationView()
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            map?.clear()
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            index = tab.position
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM.toFloat()))
        }
    }

    override fun onPermissionGranted(alreadyHadPermission: Boolean) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }
    override fun onProviderEnabled(provider: String?) { }
    override fun onProviderDisabled(provider: String?) { }
    override fun onProcessTypeChanged(processType: Int) { }
    override fun onLocationFailed(type: Int) { }

    companion object {
        private val ZOOM = 18

        private val BUILDING_NAMES = arrayOf(
            "Digital Computing Laboratory",
            "Electrical and Computer Engineering Building",
            "Thomas M. Siebel Center for Computer Science",
            "Kenney Gym"
        )
        private val BUILDING_LOCATIONS = arrayOf(
            LatLng(40.1131428, -88.2265095),
            LatLng(40.1149213, -88.2280232),
            LatLng(40.1138194, -88.2250361),
            LatLng(40.1130590, -88.2276450)
        )
    }
}
