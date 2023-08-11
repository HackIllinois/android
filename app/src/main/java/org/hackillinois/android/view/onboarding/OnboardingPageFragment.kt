package org.hackillinois.android.view.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_onboarding_page.*
import org.hackillinois.android.R

class OnboardingPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_onboarding_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageRes = requireArguments().getInt(IMAGE_RES_KEY)
        val titleRes = requireArguments().getInt(TITLE_RES_KEY)
        val descriptionRes = requireArguments().getInt(DESCRIPTION_RES_KEY)
        image_view.setImageResource(imageRes)
        title_txt.setText(titleRes)
        description_txt.setText(descriptionRes)
    }

    companion object {
        private const val IMAGE_RES_KEY = "image_res"
        private const val TITLE_RES_KEY = "title_res"
        private const val DESCRIPTION_RES_KEY = "description_res"

        fun newInstance(
            @DrawableRes imageRes: Int,
            @StringRes titleRes: Int,
            @StringRes descriptionRes: Int,
        ) = OnboardingPageFragment().apply {
            arguments = Bundle().apply {
                putInt(IMAGE_RES_KEY, imageRes)
                putInt(TITLE_RES_KEY, titleRes)
                putInt(DESCRIPTION_RES_KEY, descriptionRes)
            }
        }
    }
}
