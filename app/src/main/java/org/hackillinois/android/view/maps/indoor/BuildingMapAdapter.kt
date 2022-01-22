package org.hackillinois.android.view.maps.indoor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import org.hackillinois.android.R

class BuildingMapAdapter(
    contextArg: Context,
    private val namesAndImages: List<Pair<String, Int>>
) : ArrayAdapter<Pair<String, Int>>(contextArg, 0, namesAndImages) {

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.indoor_maps_floor_item, container, false)
        }

        val nameAndImageResource: Pair<String, Int> = namesAndImages[position]

        val floorNameTextView = view?.findViewById<TextView>(R.id.level_text)
        floorNameTextView?.text = nameAndImageResource.first

        val floorImageView = view?.findViewById<SubsamplingScaleImageView>(R.id.map_image_view)
        floorImageView?.setMinimumTileDpi(160)
        floorImageView?.setImage(ImageSource.resource(nameAndImageResource.second))

        return view!!
    }

    override fun isEnabled(position: Int) = false
}
