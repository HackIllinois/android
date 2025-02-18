package org.hackillinois.android.view.shop

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_point_shop.number_of_coins_textview
import kotlinx.android.synthetic.main.fragment_point_shop.view.recyclerview_point_shop
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.ShopItem
import org.hackillinois.android.viewmodel.ShopViewModel

class ShopFragment : Fragment(), ShopAdapter.OnBuyItemListener {

    companion object {
        fun newInstance() = ShopFragment()
    }

    private lateinit var shopViewModel: ShopViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: ShopAdapter

    private lateinit var sticker1ImageView: ImageView
    private lateinit var sticker2ImageView: ImageView
    private lateinit var sticker1TextView: TextView
    private lateinit var sticker2TextView: TextView
    private lateinit var priceTextView1: TextView
    private lateinit var priceTextView2: TextView
    private lateinit var quantityTextView1: TextView
    private lateinit var quantityTextView2: TextView

    private lateinit var merchButton: TextView
    private lateinit var raffleButton: TextView

    private var merchItems: List<ShopItem> = listOf()
    private var raffleItems: List<ShopItem> = listOf()

    // Merch tab is default selected
    private var showingMerch: Boolean = true

    private lateinit var miniTile1: View
    private lateinit var miniTile2: View

    override fun onPause() {
        super.onPause()
        shopViewModel.stopTimer()
    }

    override fun onResume() {
        super.onResume()
        shopViewModel.startTimer()
//        updateShopUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shopViewModel = ViewModelProvider(this).get(ShopViewModel::class.java)

        // pass whether the user is an attendee to the viewmodel
        if (hasLoggedIn() && isAttendee()) {
            shopViewModel.init(true)
        } else {
            shopViewModel.init(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_point_shop, container, false)

        sticker1ImageView = view.findViewById(R.id.image_view_sticker1_symbol)
        sticker2ImageView = view.findViewById(R.id.image_view_sticker2_symbol)
        sticker1TextView = view.findViewById(R.id.text_view_sticker1)
        sticker2TextView = view.findViewById(R.id.text_view_sticker2)
        priceTextView1 = view.findViewById(R.id.priceTextView1)
        priceTextView2 = view.findViewById(R.id.priceTextView2)
        quantityTextView1 = view.findViewById(R.id.quantityTextView1)
        quantityTextView2 = view.findViewById(R.id.quantityTextView2)

        merchButton = view.findViewById(R.id.merchButton)
        raffleButton = view.findViewById(R.id.raffleButton)

        recyclerView = view.recyclerview_point_shop

        val plusButton1: ImageView = view.findViewById(R.id.plusButton1)
        val plusButton2: ImageView = view.findViewById(R.id.plusButton2)
//        miniTile1 = view.findViewById(R.id.mini_tile_1)
//        miniTile2 = view.findViewById(R.id.mini_tile_2)

        shopViewModel.shopLiveData.observe(
            viewLifecycleOwner,
            Observer { shopItems ->
                // Split the shop items into Merch or Raffle category
                updateShopUI()
                updateShopItems(shopItems)
            },
        )

        merchButton.setOnClickListener(merchClickListener)
        raffleButton.setOnClickListener(raffleClickListener)

        if (hasLoggedIn() && isAttendee()) {
            // set coin views visible for attendee
            val coinBg: TextView = view.findViewById(R.id.number_of_coins_background)
            val coinText: TextView = view.findViewById(R.id.number_of_coins_textview)
            val coinImg: ImageView = view.findViewById(R.id.coin_imageview)
            coinBg.visibility = View.VISIBLE
            coinText.visibility = View.VISIBLE
            coinImg.visibility = View.VISIBLE

            shopViewModel.profileLiveData.observe(
                viewLifecycleOwner,
                Observer {
                    updateCoinTotalUI(it)
                },
            )
        }

        val cartTextView: TextView = view.findViewById(R.id.text_view_cart)
        cartTextView.bringToFront()

        cartTextView.setOnClickListener {
            Log.d("ShopFragment", "Cart image clicked")
            val cartFragment = CartFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contentFrame, cartFragment)
            transaction.addToBackStack(null) // Optional, for back navigation
            transaction.commit()
        }

        plusButton1.setOnClickListener { buyFirstItem() }
        plusButton2.setOnClickListener { buySecondItem() }


        return view
    }


    // Called in onCreateView within shopLiveData.observe
    private fun updateShopItems(newShop: List<ShopItem>) {
        // Split shop items into categories
        merchItems = newShop.filter { !it.isRaffle }.sortedBy { it.quantity == 0 }
        raffleItems = newShop.filter { it.isRaffle }.sortedBy { it.quantity == 0 }

        // **Update only the RecyclerView items**
        val recyclerViewItems = if (showingMerch) {
            if (merchItems.size > 2) merchItems.subList(2, merchItems.size) else listOf()
        } else {
            raffleItems
        }

        // Update adapter
        mAdapter.updateShop(recyclerViewItems)
    }



    private fun updateShopUI() {
        // Sort items so that out-of-stock items are pushed to the end
        val sortedItems = if (showingMerch) {
            merchItems.sortedBy { it.quantity == 0 }
        } else {
            raffleItems.sortedBy { it.quantity == 0 }
        }

        // Ensure first two items are always displayed in the fixed sticker views
        if (sortedItems.isNotEmpty()) {
            val firstItem = sortedItems[0]
            updateStickerView(firstItem, sticker1ImageView, sticker1TextView, priceTextView1, quantityTextView1)
        } else {
            clearStickerView(sticker1ImageView, sticker1TextView, priceTextView1, quantityTextView1)
        }

        if (sortedItems.size >= 2) {
            val secondItem = sortedItems[1]
            updateStickerView(secondItem, sticker2ImageView, sticker2TextView, priceTextView2, quantityTextView2)
        } else {
            clearStickerView(sticker2ImageView, sticker2TextView, priceTextView2, quantityTextView2)
        }

        // RecyclerView should only show items *after* the first two
        val recyclerViewItems =
            if (sortedItems.size > 2) sortedItems.subList(2, sortedItems.size) else listOf()

        recyclerView.apply {
            mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = mLayoutManager
            mAdapter = ShopAdapter(recyclerViewItems, this@ShopFragment)
            adapter = mAdapter
        }
    }

    // Helper function to update a sticker view
    private fun updateStickerView(
        item: ShopItem,
        imageView: ImageView,
        textView: TextView,
        priceView: TextView,
        quantityView: TextView
    ) {
        Glide.with(requireContext()).load(item.imageURL).into(imageView)
        textView.text = item.name
        priceView.text = item.price.toString()
        quantityView.text = when {
            item.isRaffle -> resources.getString(R.string.unlimited)
            item.quantity == 0 -> resources.getString(R.string.out_of_stock)
            else -> resources.getString(R.string.shopquantity, item.quantity)
        }
    }

    // Helper function to clear sticker views if not enough items are available
    private fun clearStickerView(
        imageView: ImageView,
        textView: TextView,
        priceView: TextView,
        quantityView: TextView
    ) {
        imageView.setImageDrawable(null)
        textView.text = ""
        priceView.text = ""
        quantityView.text = ""
    }



    // update merch ViewModel on click
    private val merchClickListener = View.OnClickListener {
        if (!merchButton.isSelected) {
            merchButton.isSelected = true
            merchButton.background = this.context?.let { it1 ->
                ContextCompat.getDrawable(
                    it1,
                    R.drawable.point_shop_selected_background
                )
            }
            raffleButton.isSelected = false
            raffleButton.background = this.context?.let { it1 ->
                ContextCompat.getDrawable(
                    it1,
                    R.drawable.point_shop_unselected_background
                )
            }
            showingMerch = true
            updateShopUI()
        }
    }

    // update raffle ViewModel on click
    private val raffleClickListener = View.OnClickListener {
        if (!raffleButton.isSelected) {
            raffleButton.isSelected = true
            raffleButton.background = this.context?.let { it1 ->
                ContextCompat.getDrawable(
                    it1,
                    R.drawable.point_shop_selected_background
                )
            }
            merchButton.isSelected = false
            merchButton.background = this.context?.let { it1 ->
                ContextCompat.getDrawable(
                    it1,
                    R.drawable.point_shop_unselected_background
                )
            }
            showingMerch = false
            updateShopUI()
        }
    }

    private fun updateCoinTotalUI(newProfile: Profile?) {
        if (newProfile != null) {
            number_of_coins_textview.text = String.format("%,d", newProfile.coins)
        }
    }

    private fun hasLoggedIn(): Boolean {
        // Reads JWT and checks if it is equal to an empty JWT
        return JWTUtilities.readJWT(requireActivity().applicationContext) != JWTUtilities.DEFAULT_JWT
    }

    private fun isAttendee(): Boolean {
        val context = requireActivity().applicationContext
        val prefString = context.getString(R.string.authorization_pref_file_key)
        return context.getSharedPreferences(prefString, Context.MODE_PRIVATE)
            .getString("provider", "") ?: "" == "github"
    }

    override fun onBuyItem(item: ShopItem) {
        // Implement your buying logic here (e.g., make a network call)
        lifecycleScope.launch {
            try {
                val response = App.getAPI().addItemCart(item.itemId)
                if (response.isSuccessful) {
                    // Update UI or local data with the new cart state
                    Log.d("CartDebug", "Item added: ${response.body()}")
                    Toast.makeText(requireContext(), "${item.name} redeemed successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("CartDebug", "Failed to add item: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to add item: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CartDebug", "Error adding item to cart", e)
                Toast.makeText(requireContext(), "Failed to add item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            updateShopUI()
        }
    }

    private fun buyFirstItem() {
        if (merchItems.isNotEmpty()) {
            val firstItem = merchItems[0]
            Log.d("ShopFragment", "Buying: ${firstItem.name}")
            onBuyItem(firstItem)
        }
    }

    private fun buySecondItem() {
        if (merchItems.size >= 2) {
            val secondItem = merchItems[1]
            Log.d("ShopFragment", "Buying: ${secondItem.name}")
            onBuyItem(secondItem)
        }
    }
}
