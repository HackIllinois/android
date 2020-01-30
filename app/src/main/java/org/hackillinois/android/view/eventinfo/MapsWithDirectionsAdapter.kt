package org.hackillinois.android.view.eventinfo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import org.hackillinois.android.R
import org.hackillinois.android.common.DirectionsOnClickListener
import org.hackillinois.android.database.entity.IndoorMapAndDirectionInfo

class MapsWithDirectionsAdapter(
    contextArg: Context,
    private val info: List<IndoorMapAndDirectionInfo>
) : ArrayAdapter<IndoorMapAndDirectionInfo>(contextArg, 0, info) {

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View? {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.map_with_directions_card, container, false)
        }

        val currentInfo = info[position]

        val mapView = view?.findViewById<SubsamplingScaleImageView>(R.id.map_image_view)
        mapView?.setMinimumTileDpi(160)
        mapView?.setImage(ImageSource.resource(currentInfo.indoorMapResource))

        val descriptionView = view?.findViewById<TextView>(R.id.cardLocationTextView)
        descriptionView?.text = currentInfo.locationDescription

        val directionsButton = view?.findViewById<Button>(R.id.directionsButton)
        directionsButton?.setOnClickListener(DirectionsOnClickListener(currentInfo.latLng, currentInfo.locationDescription))

        return view
    }
}
