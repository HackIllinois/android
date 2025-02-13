package org.hackillinois.android.view.shop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.ShopItem

class CartAdapter(private var cartItems: List<Pair<ShopItem, Int>>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.point_shop_cart_tile, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun getItemCount() = cartItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (item, quantity) = cartItems[position]
        bind(item, quantity, holder.itemView)
    }

    private fun bind(item: ShopItem, quantity: Int, itemView: View) {
        itemView.apply {
            val textViewSticker: TextView = findViewById(R.id.text_view_sticker)
            textViewSticker.text = item.name
            val quantiyTextView: TextView = findViewById(R.id.number_text)
            quantiyTextView.text = quantity.toString()

            val shopItemImageView: ImageView = findViewById(R.id.image_view_sticker_symbol)
            Glide.with(context).load(item.imageURL).into(shopItemImageView)
//
//            val plusButton: ImageView = findViewById(R.id.button_plus)
//            plusButton.setOnClickListener {
//                // Logic to increase quantity in the cart
//            }
        }
    }

    fun updateCart(newCartItems: List<Pair<ShopItem, Int>>) {
        this.cartItems = newCartItems
        notifyDataSetChanged()
    }
}
