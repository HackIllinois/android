package org.hackillinois.android.view.shop

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.ShopItem

class CartAdapter(
    private var cartItems: List<Pair<ShopItem, Int>>,
    private val listener: OnQuantityChangeListener
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSticker: TextView = view.findViewById(R.id.text_view_sticker)
        val quantityTextView: TextView = view.findViewById(R.id.number_text)
        val shopItemImageView: ImageView = view.findViewById(R.id.image_view_sticker_symbol)
        val plusButton: TextView = view.findViewById(R.id.button_plus)
        val minusButton: TextView = view.findViewById(R.id.button_minus)
    }

    private fun expandTouchArea(targetView: View, extraPadding: Int) {
        val parentView = targetView.parent as? ViewGroup ?: return
        parentView.post {
            val rect = Rect()
            targetView.getHitRect(rect)
            rect.top -= extraPadding
            rect.left -= extraPadding
            rect.bottom += extraPadding
            rect.right += extraPadding
            parentView.touchDelegate = TouchDelegate(rect, targetView)
            parentView.requestLayout()  // Refresh layout so it applies
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.point_shop_cart_tile, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun getItemCount() = cartItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (item, quantity) = cartItems[position]
        holder.textViewSticker.text = item.name
        holder.quantityTextView.text = quantity.toString()
        Glide.with(context).load(item.imageURL).into(holder.shopItemImageView)

        // Increase quantity using plus button
        holder.plusButton.setOnClickListener {
            val newQuantity = quantity + 1
            listener.onIncreaseQuantity(item, newQuantity)
        }

        expandTouchArea(holder.plusButton, 100)

        // Decrease quantity using minus button (allowing 0)
        holder.minusButton.setOnClickListener {
            val newQuantity = quantity - 1
            listener.onDecreaseQuantity(item, newQuantity)
        }

        expandTouchArea(holder.minusButton, 100)
    }

    fun updateCart(newCartItems: List<Pair<ShopItem, Int>>) {
        this.cartItems = newCartItems
        notifyDataSetChanged()
    }

    interface OnQuantityChangeListener {
        fun onIncreaseQuantity(item: ShopItem, newQuantity: Int)
        fun onDecreaseQuantity(item: ShopItem, newQuantity: Int)
    }
}
