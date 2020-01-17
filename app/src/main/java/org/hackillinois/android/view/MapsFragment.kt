package org.hackillinois.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.fragment_maps.*
import org.hackillinois.android.R

class MapsFragment : Fragment() {

    private lateinit var outdoorMapsButton: Button
    private lateinit var indoorMapsButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        outdoorMapsButton = view.findViewById(R.id.outdoorButton)
        indoorMapsButton = view.findViewById(R.id.indoorButton)

        outdoorMapsButton.setOnClickListener(outdoorMapsClickListener)
        indoorMapsButton.setOnClickListener(indoorMapsClickListener)

        return view
    }

    private val outdoorMapsClickListener = View.OnClickListener {
        outdoorMapsButton.isSelected = true
        indoorMapsButton.isSelected = false
    }

    private val indoorMapsClickListener = View.OnClickListener {
        outdoorMapsButton.isSelected = false
        indoorMapsButton.isSelected = true
    }

    internal class MapAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ): FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}