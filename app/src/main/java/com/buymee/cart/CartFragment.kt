package com.buymee.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.buymee.common.LoginDialog
import com.buymee.common.RegisterDialog
import com.buymee.databinding.FragmentCartBinding
import com.buymee.network.CartResponse
import com.buymee.user.LoginState
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by activityViewModels {
        InjectorUtils.provideCartViewModelFactory(requireActivity().applicationContext)
    }
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var cartItemsAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cartItemsAdapter = CartAdapter(listOf())
        cart_recycler_view.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = cartItemsAdapter
        }

        viewModel.loginLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoginState.SuccessfulLogin -> {
                    homeViewModel.setSession(true)
                    viewModel.getCart()
                }
                is LoginState.Error -> {
                    Toast.makeText(
                        context,
                        it.throwable.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.setSession(false)
                }

                is LoginState.Loading -> homeViewModel.loading()
                is LoginState.ProcessDone -> homeViewModel.processDone()
            }

        })
        homeViewModel.sessionLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isSignedIn) {
                binding.signIn.visibility = View.GONE
                binding.cartImg.visibility = View.GONE
                binding.signInText.visibility = View.GONE
                binding.cart.visibility = View.VISIBLE
            } else {
                binding.signIn.visibility = View.VISIBLE
                binding.cartImg.visibility = View.VISIBLE
                binding.signInText.visibility = View.VISIBLE
                binding.cart.visibility = View.GONE

            }
        })

        viewModel.cartFetchLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is CartState.CartFetched -> {
                    homeViewModel.setSession(true)
                    populateCarViews(viewModel.cart!!)
                    //todo viewModel.cart
                }
                is CartState.Error -> handleException(it.throwable.message)
                is CartState.Loading -> homeViewModel.loading()
                is CartState.ProcessDone -> homeViewModel.processDone()
            }
        })
        binding.signIn.setOnClickListener {
            showLoginDialog()
        }
        homeViewModel.toolBarElementsVisibility(false, isShareButtonVisible = false)
        viewModel.getCart()
    }

    private fun populateCarViews(cart: CartResponse?) {
        cartItemsAdapter.updateDataSet(
            cart?.cartItems ?: listOf()
        )
        binding.totalPrice.text = "$${cart?.total}"
    }

    private fun handleException(message: String?) {
        when (message) {
            "Unauthorized" -> {
                homeViewModel.setSession(false)
            }
            "Not Found" -> {
                Toast.makeText(context, "Your cart is empty", Toast.LENGTH_SHORT).show()
                homeViewModel.setSession(true)

            }
            else -> {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                homeViewModel.setSession(false)

            }
        }
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

}