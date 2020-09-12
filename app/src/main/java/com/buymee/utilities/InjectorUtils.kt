package com.buymee.utilities

import android.content.Context
import com.buymee.common.ProductViewModelFactory
import com.buymee.network.RetrofitInstance
import com.buymee.search.SearchViewModelFactory
import com.buymee.shops.ShopViewModelFactory
import com.buymee.shops.ShopsListViewModelFactory
import com.buymee.viewmodels.HomeViewModelFactory

object InjectorUtils {

    fun provideShopsListViewModelFactory(context: Context): ShopsListViewModelFactory {
        return ShopsListViewModelFactory(
            RetrofitInstance.api
        )
    }

    fun provideHomeViewModelFactory(context: Context): HomeViewModelFactory {
        return HomeViewModelFactory()
    }

    fun provideShopViewModelFactory(context: Context): ShopViewModelFactory {
        return ShopViewModelFactory(
            RetrofitInstance.api
        )
    }

    fun provideProductViewModelFactory(context: Context): ProductViewModelFactory {
        return ProductViewModelFactory(
            RetrofitInstance.api
        )
    }

    fun provideSearchViewModelFactory(context: Context): SearchViewModelFactory {
        return SearchViewModelFactory(
            RetrofitInstance.api
        )
    }
}
