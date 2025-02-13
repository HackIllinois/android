package org.hackillinois.android.view.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.hackillinois.android.R

class RedeemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_point_shop_redeem, container, false)

        // Handle Back Button Click
        val backButton: View = view.findViewById(R.id.title_textview_back)
        backButton.bringToFront()
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // Go back to previous fragment
        }
        return view
    }
}
