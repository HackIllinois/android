package org.hackillinois.android.view.shop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.shop_tile.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.ShopItem
import java.util.concurrent.Executors

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
            shopCardView.setBackgroundResource(R.drawable.point_shop_tile_bg)

            shopItemTextView.text = item.name
            priceTextView.text = item.price.toString()

            val quantity = item.quantity
            quantityTextView.text = resources.getString(R.string.shopquantity, quantity)
            // Declaring executor to parse the URL
            val executor = Executors.newSingleThreadExecutor()
            // Once the executor parses the URL and receives the image, handler will load it
            // in the ImageView
            val handler = Handler(Looper.getMainLooper())
            // Initializing the image
            var image: Bitmap? = null
            val imageURL = item.imageURL
            executor.execute() {
                try {
                    val `in` = java.net.URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)

                    // Only for making changes in UI
                    handler.post {
                        shopItemImageView.setImageBitmap(image)
                    }
                }
                // If the URL doesnot point to image or any other kind of failure
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateShop(shopItem: List<ShopItem>) {
        this.itemList = shopItem
        notifyDataSetChanged()
    }
}
