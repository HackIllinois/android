package org.hackillinois.android.view.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.hackillinois.android.R
import org.hackillinois.android.view.MainActivity

class StaffScannerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_staff_scanner, container, false)

        // get bottom app bar and scanner button views from the MainActivity
        val appBar = activity!!.findViewById<BottomAppBar>(R.id.bottomAppBar)
        val scannerBtn = activity!!.findViewById<FloatingActionButton>(R.id.code_entry_fab)

        // when meeting attendance button is clicked
        val meetingAttendanceButton = view.findViewById<Button>(R.id.staffMeetingBtn)
        meetingAttendanceButton.setOnClickListener {
            appBar.visibility = View.INVISIBLE
            scannerBtn.visibility = View.INVISIBLE
            val scannerFragment = ScannerFragment.newInstance("", "", true)
            (context as MainActivity).switchFragment(scannerFragment, true)
        }

        // when attendee check-in button is clicked
        val staffCheckInButton = view.findViewById<Button>(R.id.staffCheckInBtn)
        staffCheckInButton.setOnClickListener {
            appBar.visibility = View.INVISIBLE
            scannerBtn.visibility = View.INVISIBLE
            val scannerFragment = ScannerFragment.newInstance("", "", false)
            (context as MainActivity).switchFragment(scannerFragment, true)
        }

        return view
    }
}
