package com.buymee.shops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.buymee.databinding.FragmentShopMainBinding
import com.buymee.shops.ui.ShopFragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ShopMainFragment : Fragment() {

    private var _binding: FragmentShopMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()
    private lateinit var viewPager2: ViewPager2
    private lateinit var shopFragmentStateAdapter: ShopFragmentStateAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tabLayout = binding.tabLayout
        viewPager2 = binding.viewPager
        shopFragmentStateAdapter =
            ShopFragmentStateAdapter(
                arrayOf(ShopInfoFragment(), ShopProductsFragment()),
                requireActivity().supportFragmentManager,
                lifecycle
            )
        viewPager2.adapter = shopFragmentStateAdapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Shop"
                }
                1 -> {
                    tab.text = "Products"
                }
            }
        }.attach()
        viewModel.toolBarElementsVisibility(
            isBackButtonVisible = true,
            isShareButtonVisible = true,
            toolbarTitleText = viewModel.shopDetails.shopName
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}