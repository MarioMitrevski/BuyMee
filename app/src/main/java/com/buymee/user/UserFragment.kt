package com.buymee.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.buymee.common.LoginDialog
import com.buymee.common.RegisterDialog
import com.buymee.data.UserPrefs
import com.buymee.databinding.FragmentUserBinding
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.HomeViewModel

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels {
        InjectorUtils.provideUserViewModelFactory(requireActivity().applicationContext)
    }
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.loginLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoginState.SuccessfulLogin -> {
                    homeViewModel.setSession(true)
                }
                is LoginState.Error -> Toast.makeText(
                    context,
                    it.throwable.message,
                    Toast.LENGTH_SHORT
                ).show()
                is LoginState.Loading -> homeViewModel.loading()
                is LoginState.ProcessDone -> homeViewModel.processDone()
            }

        })

        homeViewModel.sessionLiveData.observe( viewLifecycleOwner, Observer {
            if(it.isSignedIn){
                binding.logout.visibility = View.VISIBLE
                binding.signIn.visibility = View.GONE
            }else{
                binding.logout.visibility = View.GONE
                binding.signIn.visibility = View.VISIBLE
            }
        })
        binding.signIn.setOnClickListener {
            showLoginDialog()
        }
        binding.logout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(
                context,
                "You are now logged out!",
                Toast.LENGTH_SHORT
            ).show()
            homeViewModel.setSession(false)
        }
        if(UserPrefs.getInstance(requireContext()).getToken()!=""){
            homeViewModel.setSession(true)
        }else{
            homeViewModel.setSession(false)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}