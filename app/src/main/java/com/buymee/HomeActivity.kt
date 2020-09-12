package com.buymee

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.buymee.databinding.ActivityHomeBinding
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.HomeViewModel
import com.buymee.viewmodels.ProgressLongBackgroundProcess

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    private val viewModel: HomeViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)

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
    }

    private fun initViews() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }
}