package org.hackillinois.android.view.admin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_admin_stats.*
import kotlinx.android.synthetic.main.fragment_admin_stats.view.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.json.JSONObject
import kotlin.concurrent.thread

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
            0 -> view = createStatsView(inflater, container, savedInstanceState)
            1 -> view = createEventsView(inflater, container, savedInstanceState)
            else -> return null
        }

        return view
    }

    fun createStatsView(inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_stats, container, false)

        view.queryBtn.setOnClickListener {
            thread {
                val response = App.getAPI().stats.execute()
                response.body()?.let {
                    activity?.runOnUiThread {
                        val data = JSONObject(it.string()).toString(4)
                        statsText.text = data
                    }
                }
            }
        }

        return view
    }

    fun createEventsView(inflater: LayoutInflater, container: ViewGroup?,
                         savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_events, container, false)

        return view
    }
}