package org.hackillinois.android.view.groupmatching

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.hackillinois.android.R
import org.hackillinois.android.view.MainActivity

class MatchingProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var skillsLayout: LinearLayout
    private lateinit var nameText: TextView
    private lateinit var teamStatusText: TextView
    private lateinit var timezoneText: TextView
    private lateinit var timeText: TextView
    private lateinit var pointsText: TextView
    private lateinit var discordText: TextView
    private lateinit var descriptionText: TextView

    private lateinit var teamStatusArray: Array<String>
    private lateinit var teamStatusVerboseArray: Array<String>
    private lateinit var teamStatusColors: Array<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        teamStatusArray = resources.getStringArray(R.array.team_status_array)
        teamStatusVerboseArray = resources.getStringArray(R.array.team_status_verbose_array)
        teamStatusColors = arrayOf(R.color.lookingForTeamColor, R.color.lookingForMembersColor, R.color.notLookingColor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matching_profile, container, false)
        profileImage = view.findViewById(R.id.profileImage)
        nameText = view.findViewById(R.id.nameText)
        teamStatusText = view.findViewById(R.id.teamStatusText)
        timezoneText = view.findViewById(R.id.timezoneText)
        timeText = view.findViewById(R.id.timeText)
        pointsText = view.findViewById(R.id.pointsText)
        discordText = view.findViewById(R.id.discordText)
        descriptionText = view.findViewById(R.id.descriptionText)
        skillsLayout = view.findViewById(R.id.skillsLinearLayout)

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.popBackStackImmediate()
        }

        descriptionText.viewTreeObserver.addOnGlobalLayoutListener(Listener(this, descriptionText))
        return view
    }

    fun updateUI() {
        val selectedProfile = (activity as MainActivity).groupMatchingSelectedProfile
                ?: return
        Glide.with(this)
                .load(selectedProfile.avatarUrl)
                .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(16)))
                .into(profileImage)

        val index: Int? = teamStatusArray.indices.firstOrNull { i -> teamStatusArray[i] == selectedProfile.teamStatus }
        index?.let {
            teamStatusText.text = "⬤ " + teamStatusVerboseArray[index]
            teamStatusText.setTextColor(resources.getColor(teamStatusColors[index]))
        }

        nameText.text = selectedProfile.firstName + " " + selectedProfile.lastName
        timezoneText.text = "time zone"
        timeText.text = selectedProfile.timezone
        pointsText.text = selectedProfile.points.toString()
        discordText.text = selectedProfile.discord
        descriptionText.text = selectedProfile.description

        val maxWidth: Int = skillsLayout.measuredWidth - 100 - 48
        // Log.i("maxWidth", maxWidth.toString())

        var params: LinearLayout.LayoutParams
        var newLL = LinearLayout(context)
        newLL.orientation = LinearLayout.HORIZONTAL
        newLL.gravity = Gravity.START
        newLL.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        var widthSoFar = 0

        selectedProfile.interests.forEach {
            val LL = LinearLayout(context)
            LL.orientation = LinearLayout.HORIZONTAL
            LL.gravity = Gravity.START
            LL.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val textView = TextView(activity)
            textView.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.text = it
            textView.setTextColor(0xFFFFFFFF.toInt())
            textView.setBackgroundResource(R.drawable.rounded_salmon_bg)
            textView.setPadding(20, 20, 20, 20)
            textView.typeface = context?.let { it1 -> ResourcesCompat.getFont(it1, R.font.montserrat_bold) }

            textView.measure(0, 0)
            params = LinearLayout.LayoutParams(textView.measuredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(20, 20, 20, 20)

            LL.addView(textView, params)
            LL.measure(0, 0)

            widthSoFar += textView.measuredWidth
            if (widthSoFar >= maxWidth) {
                skillsLayout.addView(newLL)

                newLL = LinearLayout(context)
                newLL.orientation = LinearLayout.HORIZONTAL
                newLL.gravity = Gravity.START
                newLL.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                params = LinearLayout.LayoutParams(LL.measuredWidth, LL.measuredHeight)
                newLL.addView(LL, params)
                widthSoFar = LL.measuredWidth
            } else {
                newLL.addView(LL)
            }
        }
        skillsLayout.addView(newLL)
    }
}

class Listener(private val frag: MatchingProfileFragment, private val view: View) : ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
        frag.updateUI()
        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }
}