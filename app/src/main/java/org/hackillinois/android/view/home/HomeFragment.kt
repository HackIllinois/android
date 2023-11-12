package org.hackillinois.android.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.TimeInfo
import org.hackillinois.android.viewmodel.HomeViewModel
import java.lang.Exception

class HomeFragment : Fragment(), CountdownManager.CountDownListener {

    private lateinit var daysValue: TextView
    private lateinit var hoursValue: TextView
    private lateinit var minutesValue: TextView
    private lateinit var countdownTextView: TextView

    private lateinit var viewModel: HomeViewModel
    private val countDownManager = CountdownManager(this)

    private var isActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        daysValue = view.findViewById(R.id.daysValue)
        hoursValue = view.findViewById(R.id.hoursValue)
        minutesValue = view.findViewById(R.id.minutesValue)
        countdownTextView = view.findViewById(R.id.countdownTextView)

        return view
    }

    override fun onStart() {
        super.onStart()
        isActive = true
        countDownManager.start()
    }

    override fun onPause() {
        super.onPause()
        isActive = false
        countDownManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        isActive = true
        countDownManager.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val time = App.getAPI().times()
                countDownManager.setAPITimes(time)
            } catch (e: Exception) {
            }
        }
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    override fun updateTime(timeUntil: Long) {
        val timeInfo = TimeInfo(timeUntil)

        if (isActive) {
            daysValue.text = padNumber(timeInfo.days)
            hoursValue.text = padNumber(timeInfo.hours)
            minutesValue.text = padNumber(timeInfo.minutes)
        }
    }

    override fun updateTitle(newTitle: String) {
        if (isActive) {
            countdownTextView.text = newTitle
        }
    }

    private fun padNumber(number: Long) = String.format("%02d", number)
}
