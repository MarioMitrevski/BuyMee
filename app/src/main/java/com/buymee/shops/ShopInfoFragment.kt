package com.buymee.shops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.buymee.R
import com.buymee.databinding.FragmentShopInfoBinding
import com.buymee.shops.ui.FeaturedProductsAdapter
import com.squareup.picasso.Picasso

class ShopInfoFragment : Fragment() {

    private var _binding: FragmentShopInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()
    private lateinit var adapter: FeaturedProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initFeaturedProductsRecyclerView()
    }

    private fun initViews() {
        binding.shopName.text = viewModel.shopDetails.shopName
        Picasso.get()
            .load(viewModel.shopDetails.shopLogoImage)
            .placeholder(R.color.colorCard)
            .fit()
            .into(binding.shopLogo)
    }

    private fun initFeaturedProductsRecyclerView() {
        adapter = FeaturedProductsAdapter(
            viewModel.shopDetails.products.take(4)
        ) {
            //viewModel.openProduct(it)
        }
        binding.featuredProducts.adapter = adapter
        binding.featuredProducts.layoutManager =
            GridLayoutManager(activity, 2)
        binding.featuredProducts.isNestedScrollingEnabled=false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

