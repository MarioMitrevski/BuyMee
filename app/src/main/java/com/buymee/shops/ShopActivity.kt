package com.buymee.shops

import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.buymee.databinding.ActivityShopBinding
import com.buymee.shops.ui.ShopFragmentStateAdapter
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.ProgressLongBackgroundProcess
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ShopActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var shopFragmentStateAdapter: ShopFragmentStateAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var binding: ActivityShopBinding
    private val args: ShopActivityArgs by navArgs()

    private val viewModel: ShopViewModel by viewModels {
        InjectorUtils.provideShopViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.shopDetails = ShopDetails(args.shopId, args.shopName, args.shopDescription, args.categoryId, args.createdDate, args.shopLogoImage, args.featuredProducts.toList())
        initViews()
        binding.toolbarTitle.text = viewModel.shopDetails.shopName

        viewModel.loadingLiveData.observe(this, Observer {
            when (it) {
                is ProgressLongBackgroundProcess.Loading -> {
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                }
                is ProgressLongBackgroundProcess.ProcessDone -> {
                    binding.progressBar.visibility = ProgressBar.INVISIBLE
                }
            }
        })
    }

    private fun initViews() {
        tabLayout = binding.tabLayout
        viewPager2 = binding.viewPager
        shopFragmentStateAdapter =
            ShopFragmentStateAdapter(
                arrayOf(ShopInfoFragment(), ShopProductsFragment()),
                supportFragmentManager,
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

        binding.backBtn.setOnClickListener {
            finish()
            onBackPressed()
        }
    }
}