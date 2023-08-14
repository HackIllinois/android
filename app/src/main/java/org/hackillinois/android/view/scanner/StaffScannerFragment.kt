package org.hackillinois.android.view.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.hackillinois.android.R
import org.hackillinois.android.view.MainActivity

class StaffScannerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_staff_scanner, container, false)

        // for when meeting attendance button is clicked
        val meetingAttendanceButton = view.findViewById<Button>(R.id.staffMeetingBtn)
        meetingAttendanceButton.setOnClickListener {
            val scannerFragment = ScannerFragment.newInstance("", "", true)
            (context as MainActivity).switchFragment(scannerFragment, true)
        }

        // for when attendee check-in button is clicked
        val staffCheckInButton = view.findViewById<Button>(R.id.staffCheckInBtn)
        staffCheckInButton.setOnClickListener {
            val scannerFragment = ScannerFragment.newInstance("", "", false)
            (context as MainActivity).switchFragment(scannerFragment, true)
        }

        return inflater.inflate(R.layout.fragment_staff_scanner, container, false)
    }
}
