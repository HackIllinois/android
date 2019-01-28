package org.hackillinois.android.view.admin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.hackillinois.android.R

class ToolsFragment : Fragment() {
    private val ARG_SECTION_NUM = "section_number"

    companion object {
        fun newInstance(sectionNumber: Int): ToolsFragment {
            val fragment = ToolsFragment()
            val args = Bundle()

            args.putInt(ToolsFragment().ARG_SECTION_NUM, sectionNumber)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View?

        val sectionNumber = if (arguments == null) 0 else arguments!!.getInt(ARG_SECTION_NUM)

        when (sectionNumber) {
            0 -> view = inflater.inflate(R.layout.fragment_admin_stats, container, false)
            1 -> return null
            else -> return null
        }

        return view
    }
}