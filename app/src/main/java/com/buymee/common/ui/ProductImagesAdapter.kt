package com.buymee.common.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.buymee.R
import com.squareup.picasso.Picasso

class ProductImagesAdapter(
    private var productImagesList: List<String>
) : RecyclerView.Adapter<ProductImagesAdapter.ProductImagesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductImagesAdapter.ProductImagesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_image_item, parent, false)
        return ProductImagesViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductImagesAdapter.ProductImagesViewHolder,
        position: Int
    ) {
        holder.bind(productImagesList[position])
    }

    inner class ProductImagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productImageView: ImageView = view.findViewById(R.id.product_image)


        fun bind(productImage: String) {
            Picasso.get()
                .load(productImage)
                .placeholder(R.color.colorCard)
                .fit()
                .into(productImageView)
        }
    }

    fun updateDataSet(newDataSet: List<String>){
        productImagesList = newDataSet.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productImagesList.size
    }
}
