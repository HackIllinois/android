package org.hackillinois.android.view.groupmatching

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.model.Group
import org.hackillinois.android.view.profile.ProfileViewModel


class GroupmatchingFragment : Fragment() {

    companion object {
        fun newInstance() = GroupmatchingFragment()
    }

    private lateinit var viewModel: GroupmatchingViewModel
    private lateinit var popupWindow: PopupWindow
    private lateinit var groupStatusButton: Button
    private var lookingForTeamFlag: Boolean = true
    private var lookingForMemberFlag: Boolean = true
    private lateinit var skills : Array<String>
    private lateinit var skillsChecked : BooleanArray
    private val groupAdapter = GroupAdapter()
    private lateinit var allProfiles : List<Profile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GroupmatchingViewModel::class.java)
        viewModel.init()
        viewModel.allProfilesLiveData.observe(this, Observer {
            allProfiles = it
            filterProfiles()
        })
    }

    fun filterProfiles() {
        if (lookingForTeamFlag && lookingForMemberFlag) {
            groupAdapter.data = allProfiles
        } else if (lookingForTeamFlag) {
            groupAdapter.data = allProfiles.filter { profile -> profile.teamStatus.equals("LOOKING_FOR_TEAM") }
        } else if (lookingForMemberFlag) {
            groupAdapter.data = allProfiles.filter { profile -> profile.teamStatus.equals("LOOKING_FOR_MEMBERS") }
        } else {
            groupAdapter.data = allProfiles
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.groupmatching_fragment, container, false)
        groupStatusButton = view.findViewById(R.id.group_status_button)
        val width: Int = (158 * requireContext().resources.displayMetrics.density).toInt()
        val popupView = inflater.inflate(R.layout.group_status_popup, null)
        popupWindow = PopupWindow(popupView,
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true)
        groupStatusButton.setOnClickListener {
            popupWindow.showAsDropDown(groupStatusButton)
        }
        val lookingForTeamLL = popupView.findViewById<LinearLayout>(R.id.looking_for_team_linearlayout)
        val lookingForMemberLL = popupView.findViewById<LinearLayout>(R.id.team_looking_for_members_linearlayout)
        lookingForTeamLL.setOnClickListener {
            val imageView = it.findViewById<ImageView>(R.id.looking_for_team_imageview)
            if (!lookingForTeamFlag) {
                lookingForTeamFlag = true
                imageView.setImageResource(R.drawable.filled_square)
            } else {
                lookingForTeamFlag = false
                imageView.setImageResource(R.drawable.hollow_square)
            }
            filterProfiles()
        }
        lookingForMemberLL.setOnClickListener {
            val imageView = it.findViewById<ImageView>(R.id.team_looking_for_members_imageview)
            if (!lookingForMemberFlag) {
                lookingForMemberFlag = true
                imageView.setImageResource(R.drawable.filled_square)
            } else {
                lookingForMemberFlag = false
                imageView.setImageResource(R.drawable.hollow_square)
            }
            filterProfiles()
        }
        skills = resources.getStringArray(R.array.skills_array)
        skillsChecked = BooleanArray(skills.size)

        val skillsButton = view.findViewById<Button>(R.id.skills_button)
        val alertDialogView = inflater.inflate(R.layout.skills_alert_dialog, null)
        val listView = alertDialogView.findViewById<ListView>(R.id.skills_listview)
        listView.adapter = SkillsAdapter(requireContext(), R.layout.skills_alert_dialog_item, skills, skillsChecked)
        val alertDialogBuilder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        alertDialogBuilder.setView(alertDialogView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        skillsButton.setOnClickListener {
            alertDialog.show()
        }
        val closeButton = alertDialogView.findViewById<ImageButton>(R.id.close_button)
        closeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        val recyclerView : RecyclerView = view.findViewById(R.id.team_matching_recyclerview)


        recyclerView.adapter = groupAdapter
        return view
    }


}