package com.buymee

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.buymee.databinding.FragmentShopsBinding
import com.buymee.utilities.InjectorUtils
import com.buymee.viewmodels.ShopsViewModel

class ShopsFragment : Fragment() {

    private var _binding: FragmentShopsBinding? = null
    private val binding get() = _binding!!

    private val shopsViewModel: ShopsViewModel by viewModels {
        InjectorUtils.provideShopsViewModelFactory(this.requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShopsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopsViewModel.getShops()
        shopsViewModel.myResponse.observe(viewLifecycleOwner, Observer {response ->
            if(response.isSuccessful){
                Log.d("Result",response.body().toString())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}