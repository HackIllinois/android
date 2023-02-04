package org.hackillinois.android.view.eventinfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.IndoorBuilding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_event_info.*
import kotlinx.android.synthetic.main.fragment_event_info.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.EventLocation
import org.hackillinois.android.viewmodel.EventInfoViewModel

class EventInfoFragment : Fragment(), OnMapReadyCallback {
    private lateinit var viewModel: EventInfoViewModel

    private val FIFTEEN_MINUTES_IN_MS = 1000 * 60 * 15
    private val siebelLatLng = LatLng(40.1138356, -88.2249052)
    private lateinit var eventId: String
    private lateinit var eventName: String
    private lateinit var googleMap: GoogleMap
    private var mapIsReady = false
    private var mapUpdated = false
    private var currentEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventId = arguments?.getString(EVENT_ID_KEY) ?: ""
        viewModel = ViewModelProviders.of(this).get(EventInfoViewModel::class.java)
        viewModel.init(eventId)
        viewModel.event.observe(
            this,
            Observer { event ->
                Log.i("EventInfoFragment", event.toString())
                currentEvent = event
                updateEventUI(currentEvent)
                if (mapIsReady && !mapUpdated) {
                    setupMap()
                }
            }
        )
        viewModel.isFavorited.observe(this, Observer { updateFavoritedUI(it) })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Set starting camera position to the first event location if it exists or Seibel
        // otherwise
        val firstEventLocation = viewModel.event.value?.locations?.first()
        val initialLatLng = if (firstEventLocation != null)
            LatLng(firstEventLocation.latitude, firstEventLocation.longitude)
        else
            siebelLatLng
        val zoomLevel = 18.3f
        val initialCameraPosition: CameraPosition = CameraPosition.Builder()
            .target(initialLatLng)
            .zoom(zoomLevel)
            .build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialCameraPosition))
        googleMap.setMinZoomPreference(15f)
        googleMap.isIndoorEnabled = true

        mapIsReady = true
        Log.i("EventInfoFragment", "Map is ready")
        if (currentEvent != null) {
            setupMap()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_info, container, false)
        view.exit_button.setOnClickListener { activity?.onBackPressed() }
        view.favorites_button.setOnClickListener {
            viewModel.changeFavoritedState()
        }
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    private fun setupMap() {
        Log.i("EventInfoFragment", "Setting up map")
        addMarkersToMap(currentEvent!!)
        if (currentEvent!!.locations.isNotEmpty()) {
            moveCameraToLocation(currentEvent!!.locations.first())
//                goToLevelOfFocusedBuilding(getLevelFromLocation(it.locations.first().tags))
        }
        mapUpdated = true
    }

    private fun moveCameraToLocation(location: EventLocation) {
        val locationLatLng = LatLng(location.latitude, location.longitude)
        val zoomLevel = 18.3f
        val cameraPosition = CameraPosition.Builder()
            .target(locationLatLng)
            .zoom(zoomLevel)
            .build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun getLevelFromLocation(locationString: String): Int {
        return Integer.parseInt(locationString.last().toString())
    }

    private fun goToLevelOfFocusedBuilding(level: Int) {
        val building: IndoorBuilding? = googleMap.focusedBuilding
        if (building != null && building.levels.size > level) {
            building.levels[level].activate()
        }
    }

    private fun addMarkersToMap(event: Event) {
        Log.i("EventInfoFragment", "Adding Markers to map")
        event.locations.forEach { eventLocation ->
            val latLng = LatLng(eventLocation.latitude, eventLocation.longitude)
            var marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(eventLocation.description)
            )
            Log.i("Map", marker.toString())
        }

        Log.i("Map", viewModel.event?.value.toString())
        Log.i("Map", viewModel.event?.value?.locations.toString())
        Log.i("Map", "hello")
    }

    private fun updateEventUI(event: Event?) {
        event?.let {
            event_name.text = it.name
            event_points.text = "+ ${it.points} pts"
//            event_sponsor.text = "Sponsored by ${it.sponsor}"
//            event_sponsor.visibility = if (it.sponsor.isEmpty()) View.GONE else View.VISIBLE
            event_time.text = if (it.isAsync) "Asynchronous event" else "${it.getStartTimeOfDay()} - ${it.getEndTimeOfDay()}"
            event_description.text = it.description
            if (it.eventType == "QNA") {
                event_type.text = "Q&A"
            } else {
                val eventTypeString = it.eventType.lowercase()
                event_type.text = eventTypeString.replaceFirst(eventTypeString.first(), eventTypeString.first().uppercaseChar())
            }
        }
    }

    private fun updateFavoritedUI(isFavorited: Boolean?) {
        isFavorited?.let {
            exit_button.isSelected = isFavorited
            val imageResource =
                if (isFavorited) R.drawable.ic_star_filled else R.drawable.ic_star_border
            favorites_button.setImageResource(imageResource)
        }
    }

    companion object {
        val EVENT_ID_KEY = "event_id"

        fun newInstance(eventId: String): EventInfoFragment {
            val fragment = EventInfoFragment()
            val args = Bundle().apply {
                putString(EVENT_ID_KEY, eventId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
