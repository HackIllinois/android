package org.hackillinois.android.view.shop

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_point_shop.coin_total_textview
import kotlinx.android.synthetic.main.fragment_point_shop.view.recyclerview_point_shop
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.ShopItem
import org.hackillinois.android.viewmodel.ShopViewModel

class ShopFragment : Fragment() {

    companion object {
        fun newInstance() = ShopFragment()
    }

    private lateinit var viewModel: ShopViewModel
    private var shop: List<ShopItem> = listOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: ShopAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ShopViewModel::class.java)

        // pass whether the user is an attendee to the viewmodel
        if (hasLoggedIn() && isAttendee()) {
            viewModel.init(true)
        } else {
            viewModel.init(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_point_shop, container, false)

        mAdapter = ShopAdapter(shop)

        recyclerView = view.recyclerview_point_shop.apply {
            mLayoutManager = LinearLayoutManager(context)
            this.layoutManager = mLayoutManager
            this.adapter = mAdapter
            addDividers()
        }

        viewModel.shopLiveData.observe(
            viewLifecycleOwner,
            Observer {
                updateShop(it)
            },
        )

        if (hasLoggedIn() && isAttendee()) {
            // set coin views visible for attendee
            val coinBg: TextView = view.findViewById(R.id.total_coin_view)
            val coinText: TextView = view.findViewById(R.id.coin_total_textview)
            val coinImg: ImageView = view.findViewById(R.id.coin_imageview)
            coinBg.visibility = View.VISIBLE
            coinText.visibility = View.VISIBLE
            coinImg.visibility = View.VISIBLE

            viewModel.profileLiveData.observe(
                viewLifecycleOwner,
                Observer {
                    updateCoinTotalUI(it)
                },
            )
        }

        return view
    }

    class DividerItemDecorator(context: Context) : RecyclerView.ItemDecoration() {
        private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.point_shop_divider)!!
        override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val dividerLeft = parent.paddingLeft
            val dividerRight = parent.width - parent.paddingRight
            val childCount = parent.childCount // how many items recyclerview has
            for (i in 0..childCount - 1) { // minus 2 to account for zero index and skip last item
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + mDivider.intrinsicHeight
                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                mDivider.draw(canvas)

                if (i == 0) {
                    val topDividerTop = parent.paddingTop
                    val topDividerBottom = topDividerTop + mDivider.intrinsicHeight
                    mDivider.setBounds(dividerLeft, topDividerTop, dividerRight, topDividerBottom)
                    mDivider.draw(canvas)
                }
            }
        }
    }
    fun RecyclerView.addDividers() {
        if (layoutManager !is LinearLayoutManager) {
            return
        }

        addItemDecoration(DividerItemDecorator(context))
    }

    private fun updateShop(newShop: List<ShopItem>) {
        mAdapter.updateShop(newShop)
    }

    private fun updateCoinTotalUI(newProfile: Profile?) {
        if (newProfile != null) {
            coin_total_textview.text = String.format("%,d", newProfile.coins)
        }
    }

    private fun hasLoggedIn(): Boolean {
        // Reads JWT and checks if it is equal to an empty JWT
        return JWTUtilities.readJWT(requireActivity().applicationContext) != JWTUtilities.DEFAULT_JWT
    }

    private fun isAttendee(): Boolean {
        val context = requireActivity().applicationContext
        val prefString = context.getString(R.string.authorization_pref_file_key)
        return context.getSharedPreferences(prefString, Context.MODE_PRIVATE).getString("provider", "") ?: "" == "github"
    }
}

// var itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
// itemDecoration.setDrawable(getDrawable(this.context, R.drawable.leaderboard_divider)!!)
// addItemDecoration(itemDecoration)
