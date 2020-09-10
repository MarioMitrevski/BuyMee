package com.buymee.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.buymee.common.ui.ProductImagesAdapter
import com.buymee.databinding.FragmentProductBinding
import com.buymee.network.ProductDetailsDTO
import com.buymee.shops.ShopViewModel
import com.buymee.utilities.InjectorUtils

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var imagesAdapter: ProductImagesAdapter
    private val shopViewModel: ShopViewModel by activityViewModels()
    private val viewModel: ProductViewModel by activityViewModels {
        InjectorUtils.provideProductViewModelFactory(requireActivity().applicationContext)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ProductFetchState.Loading -> shopViewModel.loading()
                is ProductFetchState.ProcessDone -> shopViewModel.processDone()
                is ProductFetchState.Error -> {
                    Toast.makeText(
                        context,
                        it.throwable.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().onBackPressed()
                }
            }
        })

        viewModel.productDTOliveData.observe(viewLifecycleOwner, Observer {
            populateProductViews(it)
        })
        viewModel.openProduct(shopViewModel.selectedProductId)
    }

    private fun initViews() {
        imagesAdapter = ProductImagesAdapter(listOf())
        binding.productImages.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = imagesAdapter
        }
    }

    private fun populateProductViews(product: ProductDetailsDTO) {
        imagesAdapter.updateDataSet(listOf(product.imagesUrls[0],product.imagesUrls[0],product.imagesUrls[0],product.imagesUrls[0],product.imagesUrls[0],product.imagesUrls[0]))
        binding.productName.text = product.productName
        binding.productDescription.text = product.productDescription
        binding.productPrice.text = "$${product.price.toInt()}"
        
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}