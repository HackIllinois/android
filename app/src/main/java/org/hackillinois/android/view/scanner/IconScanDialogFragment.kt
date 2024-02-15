package org.hackillinois.android.view.scanner

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.hackillinois.android.R

class IconScanDialogFragment : DialogFragment() {

    lateinit var iconType: String
    lateinit var subtitle: String

    interface OnIconOKButtonSelected {
        fun continueScanningAfterIconDialog()
    }

    private var onOKButtonSelectedListener: OnIconOKButtonSelected? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.dialog_icon_scan, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        iconType = arguments?.getString(ICON_TYPE).toString()
        subtitle = arguments?.getString(KEY_SUBTITLE).toString()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set onClickListener on "OK" button
        val okButton = view.findViewById<TextView>(R.id.okIconDialog_textView)
        okButton.setOnClickListener {
            dialog!!.dismiss()
        }

        // update text of title and subtitle views from bundle arguments
        val iconView = view.findViewById<ImageView>(R.id.imageIconDialog_imageView)
        val titleView = view.findViewById<TextView>(R.id.titleIconDialog_textView)
        val subtitleView = view.findViewById<TextView>(R.id.subtitleIconDialog_textView)

        if (iconType == "prize") {
            iconView.setImageResource(R.drawable.prize_bag)
            titleView.setText(R.string.prize_obtained)
        } else {
            iconView.setImageResource(R.drawable.treasure_chest)
            titleView.setText(R.string.points_earned)
        }
        subtitleView.text = subtitle
    }

    override fun onDismiss(dialog: DialogInterface) {
        onOKButtonSelectedListener?.continueScanningAfterIconDialog()
        super.onDismiss(dialog)
    }

    fun setIconOKButtonListener(listener: OnIconOKButtonSelected) {
        onOKButtonSelectedListener = listener
    }

    companion object {
        const val TAG = "IconScanDialogFragment"

        private const val ICON_TYPE = "KEY_ICON_TYPE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"

        fun newInstance(title: String, subTitle: String): IconScanDialogFragment {
            val fragment = IconScanDialogFragment()
            val args = Bundle().apply {
                putString(ICON_TYPE, title)
                putString(KEY_SUBTITLE, subTitle)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
