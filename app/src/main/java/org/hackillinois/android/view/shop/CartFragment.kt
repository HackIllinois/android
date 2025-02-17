package org.hackillinois.android.view.shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Cart
import org.hackillinois.android.database.entity.ShopItem

class CartFragment : Fragment(), CartAdapter.OnBuyItemListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private var cartItems: List<Pair<ShopItem, Int>> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_point_shop_cart, container, false)

        recyclerView = view.findViewById(R.id.recyclerview_point_shop)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        cartAdapter = CartAdapter(cartItems, this)
        recyclerView.adapter = cartAdapter

        fetchCartData()

        val backButton: View = view.findViewById(R.id.backButton)
        backButton.bringToFront()
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // Go back to the previous fragment
        }

        // Handle Redeem Button Click
        val redeemButton: View = view.findViewById(R.id.redeemButton)
        redeemButton.setOnClickListener {
            val redeemFragment = RedeemFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame, redeemFragment)
                .addToBackStack(null) // Allows back navigation
                .commit()
        }

        return view
    }

    private fun fetchCartData() {
        lifecycleScope.launch {
            try {
                // First, get all available shop items
                val shopItems: List<ShopItem> = App.getAPI().shop()

                // Next, get the cart from the API
                val cart: Cart = App.getAPI().getCart()
                val items = mutableListOf<Pair<ShopItem, Int>>()

                // Iterate over the map of itemId -> quantity from the cart.
                for ((itemId, quantity) in cart.items) {
                    // Look up the ShopItem from the shopItems list using itemId
                    val shopItem = shopItems.find { it.itemId == itemId }
                    if (shopItem != null) {
                        items.add(Pair(shopItem, quantity))
                    } else {
                        Log.e("CartFragment", "ShopItem not found for itemId: $itemId")
                    }
                }
                cartItems = items
                cartAdapter.updateCart(cartItems)
            } catch (e: Exception) {
                Log.e("CartFragment", "Error fetching cart items", e)
            }
        }
    }

    override fun onBuyItem(item: ShopItem) {
        lifecycleScope.launch {
            try {
                val response = App.getAPI().addItemCart(item.itemId)
                if (response.isSuccessful) {
                    // Update UI or local data with the new cart state
                    fetchCartData()
                    Log.d("CartDebug", "Item added: ${response.body()}")
                } else {
                    Log.e("CartDebug", "Failed to add item: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CartDebug", "Error adding item to cart", e)
            }
        }
    }
}
