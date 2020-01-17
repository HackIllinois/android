package org.hackillinois.android.view

import android.os.Bundle
import androidx.fragment.app.Fragment

class SpecificMapFragment : Fragment() {

    fun createInstance(imageTitles: List<String>, imageResources: List<Int>): SpecificMapFragment {
        val fragment = SpecificMapFragment()

        val bundle = Bundle()
        bundle.putStringArray("imageTitles", imageTitles.toTypedArray())
        bundle.putIntArray("imageResources", imageResources.toIntArray())
        fragment.arguments = bundle

        return fragment
    }
}