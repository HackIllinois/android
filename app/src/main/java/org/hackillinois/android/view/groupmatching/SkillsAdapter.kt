package org.hackillinois.android.view.groupmatching

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.hackillinois.android.R

class SkillsAdapter(context: Context, resource: Int, private val names: Array<String>,
                    private val checked: BooleanArray) : ArrayAdapter<String>(context, resource, names) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.skills_alert_dialog_item, null)
        val textView = view.findViewById<TextView>(R.id.skills_item_textview)
        textView.text = names[position]
        val imageView = view.findViewById<ImageView>(R.id.skills_check)
        imageView.visibility = if (checked[position]) View.VISIBLE else View.INVISIBLE
        val itemView = view.findViewById<LinearLayout>(R.id.skills_item)
        itemView.setOnClickListener {
            if (checked[position]) {
                checked[position] = false
                imageView.visibility = View.INVISIBLE
            } else {
                checked[position] = true
                imageView.visibility = View.VISIBLE
            }
        }
        return view
    }
}