package com.buymee.shops

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.buymee.databinding.FragmentShopsBinding
import com.buymee.shops.ui.ShopsAdapter
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ShopListFragment : Fragment() {

    private lateinit var query: String
    private var _binding: FragmentShopsBinding? = null
    private val binding get() = _binding!!
    private var searchJob: Job? = null
    private var stateJob: Job? = null
    private lateinit var adapter: ShopsAdapter

    private val viewModel: ShopsListViewModel by activityViewModels {
        InjectorUtils.provideShopsListViewModelFactory(this.requireContext().applicationContext)
    }
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initShopsRecyclerView()
        query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        initSearch()
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ShopFetchState.Loading -> homeViewModel.loading()
                is ShopFetchState.ProcessDone -> homeViewModel.processDone()
                is ShopFetchState.Error -> Toast.makeText(
                    context,
                    it.throwable.message,
                    Toast.LENGTH_SHORT
                ).show()
                is ShopFetchState.ShopFetched -> {
                    val action = ShopListFragmentDirections.actionShopsFragmentToShopActivity(
                        it.shopDetailsDTO.shopId,
                        it.shopDetailsDTO.shopName,
                        it.shopDetailsDTO.shopDescription,
                        it.shopDetailsDTO.categoryId,
                        it.shopDetailsDTO.createdDate,
                        it.shopDetailsDTO.shopLogoImage,
                        it.shopDetailsDTO.products.content.toTypedArray()
                    )
                    try {
                        findNavController().navigate(action)
                    } catch (ex: Exception) {
                        Log.d("err", ex.message.toString())
                    }
                }
            }
        })
        homeViewModel.toolBarElementsVisibility(false, isShareButtonVisible = false)
    }

    override fun onStart() {
        super.onStart()
        search(query)
    }

    private fun initShopsRecyclerView() {
        adapter = ShopsAdapter {
            viewModel.openShop(it)
        }
        binding.shopList.adapter = adapter
        binding.shopList.layoutManager = GridLayoutManager(activity, 2)
    }

    private fun search(query: String) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        stateJob?.cancel()
        hideKeyboard()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchShop(query).collectLatest {
                Log.d("Search", it.toString())
                adapter.submitData(it)
            }
        }

        stateJob = viewLifecycleOwner.lifecycleScope.launch {
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

    private fun initSearch() {
        binding.searchShop.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateShopListFromInput()
                true
            } else {
                false
            }
        }
        binding.searchShop.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateShopListFromInput()
                true
            } else {
                false
            }
        }

        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.shopList.scrollToPosition(0) }
        }
    }

    private fun updateShopListFromInput() {
        binding.searchShop.text.trim().let {
            search(it.toString())
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = activity?.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken, 0
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = ""
    }
}