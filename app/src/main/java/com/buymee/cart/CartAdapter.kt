package com.buymee.cart


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buymee.R
import com.squareup.picasso.Picasso


class CartAdapter : RecyclerView.Adapter<CartAdapter.MyViewHolder>(){

    private val  myProducts = listOf<String>("Product1", "Product2", "Product3")

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var productName: TextView = itemView.findViewById(R.id.productItemName)
        val ivBasicImage : ImageView = itemView.findViewById(R.id.productItemImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false) as View
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageUri = "https://i.imgur.com/tGbaZCY.jpg"
        Picasso.get().load(imageUri).into(holder.ivBasicImage)
        holder.productName.text = myProducts[position]
    }

    override fun getItemCount(): Int {
        return myProducts.size
    }
}