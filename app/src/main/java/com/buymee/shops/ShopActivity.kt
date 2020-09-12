package com.buymee.shops

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.buymee.databinding.ActivityShopBinding
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.ProgressLongBackgroundProcess

class ShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopBinding
    private val args: ShopActivityArgs by navArgs()

    private val viewModel: ShopViewModel by viewModels {
        InjectorUtils.provideShopViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.shopDetails = ShopDetails(
            args.shopId,
            args.shopName,
            args.shopDescription,
            args.categoryId,
            args.createdDate,
            args.shopLogoImage,
            args.featuredProducts.toList()
        )
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

        viewModel.toolbarLiveData.observe(this, Observer {
            if (it.isShareButtonVisible) binding.backBtn.visibility =
                View.VISIBLE else binding.backBtn.visibility = View.INVISIBLE
            if (it.isShareButtonVisible) binding.shareBtn.visibility =
                View.VISIBLE else binding.shareBtn.visibility = View.INVISIBLE
            binding.toolbarTitle.text = it.toolbarTitleText
        })
        viewModel.toolBarElementsVisibility(
            isBackButtonVisible = true,
            isShareButtonVisible = true,
            toolbarTitleText = viewModel.shopDetails.shopName
        )
    }

    private fun initViews() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }
}