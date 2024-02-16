package org.hackillinois.android.view.shop

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.shop_tile.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.ShopItem

class ShopAdapter(private var itemList: List<ShopItem>) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {
    private lateinit var context: Context
    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    // onCreateViewHolder used to display scrollable list of items
    // implemented as part of RecyclerView's adapter, responsible for creating new ViewHolder objects
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResource = R.layout.shop_tile
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun getItemCount() = itemList.size

    // onBindViewHolder called when ViewHolder needs to be filled with data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        // populating views within ViewHolder with data from 'item'
        // position is zero-indexed but we want the leaderboard to start at 1
        bind(item, holder.itemView, position + 1)
    }

    private fun bind(item: ShopItem, itemView: View, position: Int) {
        itemView.apply {
            // set on click listener on item price + quantity "button" section
            shopItemListenerView.setOnClickListener {
                Toast.makeText(itemView.context, R.string.shop_toast_text, Toast.LENGTH_SHORT).show()
            }

            // set the top brown divider for the first item to be visible
            if (position == 1) {
                val topDivider: TextView = itemView.findViewById(R.id.brownDividerTop)
                topDivider.visibility = View.VISIBLE
            } else {
                val topDivider: TextView = itemView.findViewById(R.id.brownDividerTop)
                topDivider.visibility = View.GONE
            }

            shopItemTextView.text = item.name
            priceTextView.text = item.price.toString()

            val quantity = item.quantity
            if (item.isRaffle) {
                quantityTextView.text = resources.getString(R.string.unlimited)
            } else {
                quantityTextView.text = resources.getString(R.string.shopquantity, quantity)
            }

            val shopItemImageView: ImageView = itemView.findViewById(R.id.shopItemImageView)
            try {
                Glide.with(context).load(item.imageURL).into(shopItemImageView)
            } catch (e: Exception) {
                Log.d("Shop Glide Error", e.message.toString())
            }
        }
    }

    fun updateShop(shopItem: List<ShopItem>) {
        this.itemList = shopItem
        notifyDataSetChanged()
    }
}
