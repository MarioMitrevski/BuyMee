package com.buymee.shops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.buymee.R
import com.buymee.databinding.FragmentShopProductsBinding
import com.buymee.shops.data.Order
import com.buymee.shops.data.Sort
import com.buymee.shops.ui.ShopProductsAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

class ShopProductsFragment : Fragment() {

    private var fetchJob: Job? = null
    private var stateJob: Job? = null
    private var _binding: FragmentShopProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()
    private lateinit var adapter: ShopProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initShopProductsRecyclerView()

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.filter_item,
            arrayOf("Most recent", "Oldest", "Highest price", "Lowest price")
        )
        val editTextFilledExposedDropdown: AutoCompleteTextView = binding.filterDropdown
        editTextFilledExposedDropdown.setAdapter(adapter)

        editTextFilledExposedDropdown.setOnItemClickListener { adapterView, view, i, l ->

            when (i) {
                0 -> getShopProducts(
                    viewModel.shopDetails.shopId,
                    Sort.created_date,
                    Order.DESC,
                    null
                )
                1 -> getShopProducts(
                    viewModel.shopDetails.shopId,
                    Sort.created_date,
                    Order.ASC,
                    null
                )
                2 -> getShopProducts(
                    viewModel.shopDetails.shopId,
                    Sort.price,
                    Order.DESC,
                    null
                )
                3 -> getShopProducts(
                    viewModel.shopDetails.shopId,
                    Sort.price,
                    Order.ASC,
                    null
                )
            }
        }
        getShopProducts(
            viewModel.shopDetails.shopId,
            Sort.created_date,
            Order.DESC,
            null
        )
    }

    private fun initShopProductsRecyclerView() {
        adapter = ShopProductsAdapter {
            viewModel.selectedProductId = it
            findNavController().navigate(R.id.action_shopMainFragment_to_productFragment)
        }
        binding.shopProductsList.adapter = adapter
        binding.shopProductsList.layoutManager = GridLayoutManager(activity, 2)
    }

    private fun getShopProducts(
        shopId: String,
        sort: Sort,
        order: Order,
        categoryId: Long?
    ) {
        fetchJob?.cancel()
        stateJob?.cancel()
        fetchJob = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getShopProducts(shopId, sort, order, categoryId).collectLatest {
                adapter.submitData(it)
            }
        }
        stateJob = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Loading -> viewModel.loading()
                    is LoadState.NotLoading -> viewModel.processDone()
                    is LoadState.Error -> {
                        viewModel.processDone()
                        Toast.makeText(
                            context,
                            (loadStates.refresh as LoadState.Error).error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        stateJob?.invokeOnCompletion {
            viewModel.processDone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}