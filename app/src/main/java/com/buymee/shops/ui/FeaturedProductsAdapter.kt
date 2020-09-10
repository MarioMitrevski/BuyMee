package com.buymee.shops.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buymee.R
import com.buymee.network.FeaturedProduct
import com.squareup.picasso.Picasso

class FeaturedProductsAdapter constructor(
    private val productList: List<FeaturedProduct>,
    private val productClickListener: (String) -> Unit
) : RecyclerView.Adapter<FeaturedProductsAdapter.FeaturedProductViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeaturedProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_view_item, parent, false)
        return FeaturedProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FeaturedProductViewHolder,
        position: Int
    ) {
        holder.bind(productList[position], productClickListener)
    }

    inner class FeaturedProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productImage: ImageView = view.findViewById(R.id.product_image)
        private val productName: TextView = view.findViewById(R.id.product_name)
        private val productPrice: TextView = view.findViewById(R.id.product_price)

        fun bind(featuredProduct: FeaturedProduct, productClickListener: (String) -> Unit) {
            showProductData(featuredProduct)
            itemView.setOnClickListener {
                productClickListener(featuredProduct.productId)
            }
        }

        private fun showProductData(featuredProduct: FeaturedProduct) {
            productName.text = featuredProduct.productName
            productPrice.text = "$${featuredProduct.price.toInt()}"
            Picasso.get()
                .load(featuredProduct.imageURL)
                .placeholder(R.color.colorCard)
                .fit()
                .into(productImage)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
