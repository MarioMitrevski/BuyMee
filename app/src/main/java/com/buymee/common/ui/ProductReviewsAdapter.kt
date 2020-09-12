package com.buymee.common.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buymee.R
import com.buymee.network.ProductReviewDTO
import java.text.SimpleDateFormat
import java.util.*

class ProductReviewsAdapter(
    private var productReviewsList: List<ProductReviewDTO>
) : RecyclerView.Adapter<ProductReviewsAdapter.ProductReviewsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductReviewsAdapter.ProductReviewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_product_review_item, parent, false)
        return ProductReviewsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductReviewsAdapter.ProductReviewsViewHolder,
        position: Int
    ) {
        holder.bind(productReviewsList[position])
    }

    inner class ProductReviewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userName: TextView = view.findViewById(R.id.user_name)
        private val userGrade: TextView = view.findViewById(R.id.user_grade)
        private val userComment: TextView = view.findViewById(R.id.user_comment)
        private val date: TextView = view.findViewById(R.id.date)

        fun bind(productReview: ProductReviewDTO) {
            userName.text = "${productReview.firstName} ${productReview.lastName}"
            userGrade.text = "(${productReview.grade})"
            userComment.text = productReview.comment
            val srcDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val d = srcDf.parse(productReview.createdDate)
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = d.time
            date.text = "${calendar.get(Calendar.DAY_OF_MONTH)}, ${calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,Locale.getDefault())} ${calendar.get(Calendar.YEAR)}"
        }
    }

    fun updateDataSet(newDataSet: List<ProductReviewDTO>) {
        productReviewsList = newDataSet.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productReviewsList.size
    }
}
