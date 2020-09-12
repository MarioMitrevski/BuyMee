package com.buymee.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buymee.R
import com.buymee.network.ShopProduct
import com.squareup.picasso.Picasso

class ShopProductsAdapter constructor(
    private val shopProductClickListener: (String) -> Unit
) : PagingDataAdapter<ShopProduct, ShopProductsAdapter.ShopProductViewHolder>(
    SHOP_PRODUCT_COMPARATOR
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShopProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_view_item, parent, false)
        return ShopProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ShopProductViewHolder,
        position: Int
    ) {
        val shopProductItem = getItem(position)
        shopProductItem?.run {
            holder.bind(this, shopProductClickListener)
        }
    }

    inner class ShopProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productImage: ImageView = view.findViewById(R.id.product_image)
        private val productName: TextView = view.findViewById(R.id.product_name)
        private val productPrice: TextView = view.findViewById(R.id.product_price)

        fun bind(shopProduct: ShopProduct, shopProductClickListener: (String) -> Unit) {
            showShopProductData(shopProduct)
            productImage.setOnClickListener {
                shopProductClickListener(shopProduct.productId)
            }
        }

        private fun showShopProductData(shopProduct: ShopProduct) {
            productName.text = shopProduct.productName
            productPrice.text = "$${shopProduct.price.toInt()}"

            Picasso.get()
                .load(shopProduct.imageURL)
                .placeholder(R.color.colorCard)
                .fit()
                .into(productImage)

        }
    }

    companion object {
        private val SHOP_PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<ShopProduct>() {
            override fun areItemsTheSame(oldItem: ShopProduct, newItem: ShopProduct): Boolean =
                oldItem.productId == newItem.productId

            override fun areContentsTheSame(oldItem: ShopProduct, newItem: ShopProduct): Boolean =
                oldItem == newItem
        }
    }
}
