package com.buymee.shops.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ShopFragmentStateAdapter(
    private val shopTabs: Array<Fragment>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = shopTabs.size

    override fun createFragment(position: Int): Fragment {
        return shopTabs[position]
    }
}
