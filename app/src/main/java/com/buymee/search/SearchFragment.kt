package com.buymee.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.buymee.R
import com.buymee.databinding.FragmentSearchBinding
import com.buymee.network.Category
import com.buymee.search.ui.CategoriesAdapter
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.HomeViewModel
import java.util.stream.Collectors

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoriesAdapter: CategoriesAdapter

    // private lateinit var productsAdapter: ProductsAdapter
    private val viewModel: SearchViewModel by activityViewModels {
        InjectorUtils.provideSearchViewModelFactory(requireActivity().applicationContext)
    }
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {

            // Handle the back button event
            if (viewModel.categoryIdClicked != -1L) {
                //action not popBackStack
                val cat =
                    viewModel.categories.find { it.categoryId == viewModel.categoryIdClicked }
                if (cat?.superCategoryId != null) {
                    viewModel.categoryIdClicked = cat.superCategoryId
                } else {
                    viewModel.categoryIdClicked = -1L
                }
                populateCategoriesList(viewModel.categories)
            } else {
                isEnabled = false
                activity?.onBackPressed()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(homeViewModel.isDeepLink){
            findNavController().navigate(R.id.action_search_fragment_to_productsFragment)
        }
        initRecyclerViews()
        initViewAllBtn()
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkFetchState.Loading -> homeViewModel.loading()
                is NetworkFetchState.ProcessDone -> homeViewModel.processDone()
                is NetworkFetchState.Error -> {
                    Toast.makeText(
                        context,
                        it.throwable.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkFetchState.Fetched -> {
                    populateCategoriesList(it.categories)
                }
            }
        })
        viewModel.getCategories()
        homeViewModel.toolBarElementsVisibility(
            isBackButtonVisible = false,
            isShareButtonVisible = false
        )

    }

    private fun initRecyclerViews() {
        categoriesAdapter = CategoriesAdapter(
            arrayListOf()
        ) {
            viewModel.categoryIdClicked = it
            populateCategoriesList(viewModel.categories)

        }
        binding.categoriesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
        }
    }

    private fun populateCategoriesList(categories: List<Category>) {
        var categorySelected: Long = viewModel.categoryIdClicked
        if (viewModel.categoryNoChildrenClicked) {
            val category =
                viewModel.categories.find { category -> category.categoryId == categorySelected }
            categorySelected = category!!.superCategoryId ?: -1L
            viewModel.categoryIdClicked = categorySelected
            viewModel.categoryNoChildrenClicked = false
        }

        if (categorySelected == -1L) {
            categoriesAdapter.updateDataSet(
                categories.parallelStream().filter { it.superCategoryId == null }
                    .collect(Collectors.toList())
            )
        } else {
            val list = categories.parallelStream().filter { it.superCategoryId == categorySelected }
                .collect(Collectors.toList())
            if (list.isEmpty()) {
                viewModel.categoryNoChildrenClicked = true
                val action =
                    SearchFragmentDirections.actionSearchFragmentToProductsFragment(viewModel.categoryIdClicked)
                findNavController().navigate(action)
            } else {
                categoriesAdapter.updateDataSet(list)
            }
        }
        binding.categories.text =
            categories.find { it.categoryId == categorySelected }?.categoryName ?: "All Categories"
    }

    private fun initViewAllBtn(){
        binding.viewAllBtn.setOnClickListener {
            val action =
                SearchFragmentDirections.actionSearchFragmentToProductsFragment(viewModel.categoryIdClicked)
            findNavController().navigate(action)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}