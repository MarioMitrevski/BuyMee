package com.buymee.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buymee.R
import com.buymee.network.Category

class CategoriesAdapter constructor(
    private var categoriesList: List<Category>,
    private val categoryClickListener: (Long) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoriesAdapter.CategoryViewHolder,
        position: Int
    ) {
        holder.bind(categoriesList[position], categoryClickListener)
    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var category: Category? = null
        private val categoryName: TextView = view.findViewById(R.id.category_name)

        fun bind(category: Category, categoryClickListener: (Long) -> Unit) {
            this.category = category
            categoryName.text = category.categoryName
            itemView.setOnClickListener {
                categoryClickListener(this.category!!.categoryId)
            }
        }
    }

    fun updateDataSet(newDataSet: List<Category>){
        categoriesList = newDataSet.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }
}
