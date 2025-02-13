package org.hackillinois.android.view.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.ShopItem

class CartFragment : Fragment() {

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

        cartAdapter = CartAdapter(cartItems)
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
        // random example to see the layout
        cartItems = listOf(
            Pair(ShopItem("1", "T-Shirt", 10, false, 1, "https://example.com/tshirt.png"), 2),
            Pair(ShopItem("2", "Sticker", 5, false, 1, "https://example.com/sticker.png"), 3)
        )
        cartAdapter.updateCart(cartItems)
    }
}
