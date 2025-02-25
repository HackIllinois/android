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
import org.hackillinois.android.view.scanner.SimpleScanDialogFragment
import org.json.JSONObject

class CartFragment : Fragment(), CartAdapter.OnQuantityChangeListener {

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
            requireActivity().supportFragmentManager.popBackStack() // Go back to previous fragment
        }

        val redeemButton: View = view.findViewById(R.id.redeemButton)
        redeemButton.setOnClickListener {
            val redeemFragment = RedeemFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contentFrame, redeemFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun fetchCartData() {
        lifecycleScope.launch {
            try {
                val shopItems: List<ShopItem> = App.getAPI().shop()
                val cart: Cart = App.getAPI().getCart()
                val items = mutableListOf<Pair<ShopItem, Int>>()

                for ((itemId, quantity) in cart.items) {
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

    override fun onIncreaseQuantity(item: ShopItem, newQuantity: Int) {
        lifecycleScope.launch {
            try {
                val response = App.getAPI().addItemCart(item.itemId)
                if (response.isSuccessful) {
                    Log.d("CartDebug", "Item added: ${response.body()}")
                    fetchCartData() // Refresh cart
                } else {
                    // Extract error message from response.errorBody()
                    val errorMessage = try {
                        val errorBody = response.errorBody()?.string()
                        if (!errorBody.isNullOrEmpty()) {
                            val jsonObject = JSONObject(errorBody)
                            jsonObject.optString("message", "Failed to add item: ${response.code()}")
                        } else {
                            "Failed to add item: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        "Failed to add item: ${response.code()}"
                    }
                    Log.e("CartDebug", "Failed to add item: $errorMessage")
                    showErrorDialog("Error", errorMessage)
                }
            } catch (e: Exception) {
                Log.e("CartDebug", "Error adding item to cart", e)
                showErrorDialog("Error", "Failed to add item: ${e.message}")
            }
        }
    }

    override fun onDecreaseQuantity(item: ShopItem, newQuantity: Int) {
        lifecycleScope.launch {
            try {
                val response = App.getAPI().removeItemCart(item.itemId)
                if (response.isSuccessful) {
                    Log.d("CartDebug", "Item removed: ${response.body()}")
                    if (newQuantity == 0) {
                        // If quantity is zero, remove the item from UI
                        fetchCartData()
                    } else {
                        // Just update the UI without full fetch
                        cartItems = cartItems.map {
                            if (it.first.itemId == item.itemId) Pair(it.first, newQuantity) else it
                        }
                        cartAdapter.updateCart(cartItems)
                    }
                } else {
                    Log.e("CartDebug", "Failed to remove item: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CartDebug", "Error removing item from cart", e)
            }
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        val args = Bundle().apply {
            putString("KEY_TITLE", title)
            putString("KEY_SUBTITLE", message)
        }
        val dialog = SimpleScanDialogFragment()
        dialog.arguments = args
        dialog.setSimpleOKButtonListener(object : SimpleScanDialogFragment.OnSimpleOKButtonSelected {
            override fun continueScanningAfterSimpleDialog() {
                // Optionally perform an action here (e.g. refresh the cart), or leave empty.
            }
        })
        dialog.show(requireActivity().supportFragmentManager, "CartErrorDialogFragment")
    }
}
