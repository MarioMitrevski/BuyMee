package com.buymee.cart


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buymee.R
import com.buymee.network.CartItem
import com.squareup.picasso.Picasso

class CartAdapter(
    private var cartItems: List<CartItem>
) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CartAdapter.CartItemViewHolder,
        position: Int
    ) {
        holder.bind(cartItems[position])
    }

    inner class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productItemImage: ImageView = view.findViewById(R.id.productItemImage)
        private val productItemName: TextView = view.findViewById(R.id.productItemName)
        private val productCartQuantity: TextView = view.findViewById(R.id.productCartQuantity)

        fun bind(cartItem: CartItem) {
            productCartQuantity.text = "$${cartItem.cartItemQuantity}"
            productItemName.text = "${cartItem.productName}"
            Picasso.get()
                .load(cartItem.imageUrl)
                .placeholder(R.color.colorCard)
                .fit()
                .into(productItemImage)
        }
    }

    fun updateDataSet(newDataSet: List<CartItem>) {
        cartItems = newDataSet.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}