package com.buymee.shops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.buymee.R
import com.buymee.common.ProductType
import com.buymee.databinding.FragmentShopInfoBinding
import com.buymee.shops.ui.FeaturedProductsAdapter
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

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
        val srcDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = srcDf.parse(viewModel.shopDetails.createdDate)
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = date.time
        binding.sinceDate.text = "Since ${calendar.get(Calendar.YEAR)}"
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
            viewModel.selectedProductId = it
            val action = ShopMainFragmentDirections.actionShopMainFragmentToProductFragment(
                viewModel.selectedProductId,
                ProductType.SHOP
            )
            findNavController().navigate(action)
        }
        binding.featuredProducts.adapter = adapter
        binding.featuredProducts.layoutManager =
            GridLayoutManager(activity, 2)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

