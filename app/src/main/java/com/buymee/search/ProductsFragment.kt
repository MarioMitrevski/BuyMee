package com.buymee.search

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
import com.buymee.common.ProductType
import com.buymee.databinding.FragmentProductsBinding
import com.buymee.search.ui.ShopProductsAdapter
import com.buymee.shops.data.Order
import com.buymee.shops.data.Sort
import com.buymee.viewmodels.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

class ProductsFragment : Fragment() {

    private var fetchJob: Job? = null
    private var stateJob: Job? = null
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ShopProductsAdapter
    private val viewModel: SearchViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
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
                0 -> getProducts(
                    Sort.created_date,
                    Order.DESC,
                    viewModel.categoryIdClicked
                )
                1 -> getProducts(
                    Sort.created_date,
                    Order.ASC,
                    viewModel.categoryIdClicked
                )
                2 -> getProducts(
                    Sort.price,
                    Order.DESC,
                    viewModel.categoryIdClicked
                )
                3 -> getProducts(
                    Sort.price,
                    Order.ASC,
                    viewModel.categoryIdClicked
                )
            }
        }
        getProducts(
            Sort.created_date,
            Order.DESC,
            viewModel.categoryIdClicked
        )
        homeViewModel.toolBarElementsVisibility(
            isBackButtonVisible = false,
            isShareButtonVisible = false
        )
    }

    private fun initShopProductsRecyclerView() {
        adapter = ShopProductsAdapter {
            val action = ProductsFragmentDirections.actionProductsFragmentToProductFragment(
                it,
                ProductType.SEARCH
            )
            findNavController().navigate(action)
        }
        binding.shopProductsList.adapter = adapter
        binding.shopProductsList.layoutManager = GridLayoutManager(activity, 2)
    }

    private fun getProducts(
        sort: Sort,
        order: Order,
        categoryId: Long?
    ) {
        fetchJob?.cancel()
        stateJob?.cancel()
        fetchJob = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getProducts(sort, order, categoryId).collectLatest {
                adapter.submitData(it)
            }
        }
        stateJob = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Loading -> homeViewModel.loading()
                    is LoadState.NotLoading -> homeViewModel.processDone()
                    is LoadState.Error -> {
                        homeViewModel.processDone()
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
            homeViewModel.processDone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}