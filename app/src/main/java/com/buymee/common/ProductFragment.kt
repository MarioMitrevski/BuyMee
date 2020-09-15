package com.buymee.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.buymee.HomeActivity
import com.buymee.R
import com.buymee.common.ui.ProductImagesAdapter
import com.buymee.common.ui.ProductReviewsAdapter
import com.buymee.databinding.FragmentProductBinding
import com.buymee.network.ProductDetailsDTO
import com.buymee.shops.ShopViewModel
import com.buymee.user.LoginState
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.HomeViewModel

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var imagesAdapter: ProductImagesAdapter
    private lateinit var reviewsAdapter: ProductReviewsAdapter
    private val args: ProductFragmentArgs by navArgs()
    private var homeViewModel: HomeViewModel? = null
    private var shopViewModel: ShopViewModel? = null
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
        if (activity?.javaClass == HomeActivity::class.java) {
            activity?.let {
                homeViewModel =
                    ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
            }
        } else {
            activity?.let {
                shopViewModel =
                    ViewModelProvider(requireActivity()).get(ShopViewModel::class.java)
            }
        }
        initViews()
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ProductFetchState.Loading -> shopViewModel?.loading() ?: homeViewModel?.loading()
                is ProductFetchState.ProcessDone -> shopViewModel?.processDone()
                    ?: homeViewModel?.processDone()
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
        viewModel.loginLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoginState.SuccessfulLogin -> {
                    homeViewModel?.setSession(true)
                    shopViewModel?.setSession(true)
                    Toast.makeText(context, "Success login", Toast.LENGTH_SHORT).show()
                }
                is LoginState.Error -> Toast.makeText(
                    context,
                    it.throwable.message,
                    Toast.LENGTH_SHORT
                ).show()
                is LoginState.Loading -> shopViewModel?.loading() ?: homeViewModel?.loading()
                is LoginState.ProcessDone -> shopViewModel?.processDone() ?: homeViewModel?.processDone()
            }

        })
        viewModel.productDTOliveData.observe(viewLifecycleOwner, Observer {
            populateProductViews(it)
            homeViewModel?.toolBarElementsVisibility(
                isBackButtonVisible = true,
                isShareButtonVisible = true,
                toolbarTitleText = it.productName
            )
            shopViewModel?.toolBarElementsVisibility(
                isBackButtonVisible = true,
                isShareButtonVisible = true,
                toolbarTitleText = it.productName
            )
        })

        viewModel.cartLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is AddToCartState.Loading -> shopViewModel?.loading() ?: homeViewModel?.loading()
                is AddToCartState.ProcessDone -> shopViewModel?.processDone() ?: homeViewModel?.processDone()
                is AddToCartState.CartSaved -> Toast.makeText(context, "Product saved to cart!", Toast.LENGTH_SHORT).show()
                is AddToCartState.Error -> handleException(it.throwable.message)
            }
        })

        if (homeViewModel != null && homeViewModel!!.isDeepLink) {
            homeViewModel!!.isDeepLink = false
            viewModel.openProduct(homeViewModel!!.selectedProductId)
            return
        }

        binding.addToCartBtn.setOnClickListener {
            if (viewModel.selectedProductItemId != "" && viewModel.selectedQuantity != "") {
                //todo add to cart
                viewModel.addToCart(viewModel.selectedProductItemId, viewModel.selectedQuantity)
            } else {
                if (viewModel.selectedQuantity == "") {
                    binding.quantity.requestFocus()
                }
                if (viewModel.selectedProductItemId == "") {
                    binding.productItems.requestFocus()
                }
            }
        }
        viewModel.openProduct(args.productId)
    }

    private fun initViews() {
        imagesAdapter = ProductImagesAdapter(listOf())
        binding.productImages.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = imagesAdapter
        }

        reviewsAdapter = ProductReviewsAdapter(listOf())
        binding.reviewsList.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            this.adapter = reviewsAdapter
        }
    }

    private fun handleException(message: String?){
        if(message.equals("Unauthorized")){
            showLoginDialog()
        }else{
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun populateProductViews(product: ProductDetailsDTO) {
        imagesAdapter.updateDataSet(
            product.imagesUrls
        )
        binding.productName.text = product.productName
        binding.productDescription.text = product.productDescription
        binding.productPrice.text = "$${product.price.toInt()}"
        binding.reviewsNumber.text = "(${product.productReviews.size})"
        val productItemsList = product.productItems.sortedBy { it.price }
            .map { it -> "${it.attributes.map { it.attributeValue }} [$${it.price}]" }.toList()
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.filter_item,
            productItemsList
        )
        val editTextFilledExposedDropdown: AutoCompleteTextView = binding.productItemsDropdown
        val quantityDropDown: AutoCompleteTextView = binding.quantityDropdown

        editTextFilledExposedDropdown.setAdapter(adapter)

        editTextFilledExposedDropdown.setOnItemClickListener { adapterView, view, i, l ->
            viewModel.selectedProductItemId =
                viewModel.productDTOliveData.value?.productItems?.get(i)?.productItemId ?: ""
            val stock =
                viewModel.productDTOliveData.value?.productItems?.get(i)?.quantityInStock ?: 0
            val quantitiesList = IntArray(stock + 1) { it }.toList()
            val quantityAdapter = ArrayAdapter(
                requireContext(),
                R.layout.filter_item,
                quantitiesList.subList(1, stock + 1)
            )
            quantityDropDown.setAdapter(quantityAdapter)
        }


        quantityDropDown.setOnItemClickListener { adapterView, view, i, l ->
            viewModel.selectedQuantity = (i + 1).toString()
        }
        reviewsAdapter.updateDataSet(product.productReviews)
    }

    private fun showLoginDialog() {
        val fm: FragmentManager = activity?.supportFragmentManager!!
        val loginDialogFragment: LoginDialog =
            LoginDialog.newInstance()
        loginDialogFragment.show(fm, "fragment_edit_name")
        loginDialogFragment.setLoginDialogListener(object :
            LoginDialog.LoginDialogListener {
            override fun onSignInClick(email: String, password: String) {
                loginDialogFragment.dismiss()
                viewModel.loginUser(email, password)
            }

            override fun onShowRegisterClick() {
                loginDialogFragment.dismiss()
                showRegisterDialog()
            }
        })
    }

    private fun showRegisterDialog() {
        val fm: FragmentManager = activity?.supportFragmentManager!!
        val registerDialogFragment: RegisterDialog =
            RegisterDialog.newInstance()
        registerDialogFragment.show(fm, "fragment_edit_name")
        registerDialogFragment.setRegisterDialogListener(object :
            RegisterDialog.RegisterDialogListener {
            override fun onRegisterClick(
                firstName: String,
                lastName: String,
                email: String,
                password: String
            ) {
                registerDialogFragment.dismiss()
                Toast.makeText(context, "$firstName $lastName $email $password", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onShowSignInClick() {
                registerDialogFragment.dismiss()
                showLoginDialog()
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}