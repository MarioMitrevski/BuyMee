package com.buymee.shops.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buymee.R
import com.buymee.network.Shop
import com.squareup.picasso.Picasso
import java.util.*

class ShopsAdapter constructor(
    private val shopClickListener: (UUID) -> Unit
) : PagingDataAdapter<Shop, ShopsAdapter.ShopViewHolder>(
    SHOP_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_view_item, parent, false)
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val shopItem = getItem(position)
        shopItem?.run {
            holder.bind(this, shopClickListener)
        }
    }

    inner class ShopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.shop_name)
        private val logoView: ImageView = view.findViewById(R.id.shop_logo)

        fun bind(shop: Shop, shopClickListener: (UUID) -> Unit) {
            showShopData(shop)
            itemView.setOnClickListener {
                shopClickListener(shop.shopId!!)
            }
        }

        private fun showShopData(shop: Shop) {
            nameView.text = shop.shopName
            shop.shopLogo?.let {
                Picasso.get()
                    .load(shop.shopLogo.toString())
                    .placeholder(R.color.colorCard)
                    .fit()
                    .into(logoView)
            }
        }
    }

    companion object {
        private val SHOP_COMPARATOR = object : DiffUtil.ItemCallback<Shop>() {
            override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean =
                oldItem.shopName == newItem.shopName

            override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean =
                oldItem == newItem
        }
    }
}
