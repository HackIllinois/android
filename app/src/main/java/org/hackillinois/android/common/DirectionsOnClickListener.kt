package org.hackillinois.android.common

import android.content.Intent
import android.net.Uri
import android.view.View
import com.google.android.gms.maps.model.LatLng

class DirectionsOnClickListener(
    private val location: LatLng,
    private val name: String
) : View.OnClickListener {
    private val URI_TEMPLATE = "http://maps.google.com/maps?q=loc:%s,%s (%s)"

    override fun onClick(view: View) {
        val uri = String.format(URI_TEMPLATE, location.latitude, location.longitude, name)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        view.context.startActivity(intent)
    }
}