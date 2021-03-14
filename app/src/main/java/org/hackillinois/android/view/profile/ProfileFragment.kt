package org.hackillinois.android.view.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var nameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })
//        Log.d("TAG", viewModel.currentProfileLiveData.value!!.firstName)
//        Log.d("TAG", "ALL PROFILES" + viewModel.allProfilesLiveData.value!!.get(0).firstName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        nameTextView = view.findViewById(R.id.nameText)

//        Log.d("TAG", viewModel.currentProfileLiveData.value!!.firstName)
//        Log.d("TAG", "ALL PROFILES" + viewModel.allProfilesLiveData.value!!.get(0).firstName)

        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let {
        nameTextView.text = it.firstName + " " + it.lastName
        Log.d("TAG", "Updated nameTextView")
    }
}