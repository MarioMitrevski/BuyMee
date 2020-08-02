package com.buymee.utilities

import android.content.Context
import com.buymee.repositories.ShopsRepository
import com.buymee.viewmodels.ShopsViewModelFactory

object InjectorUtils {

    private fun getShopsRepository(context: Context): ShopsRepository {
        return ShopsRepository.getInstance(context)
    }

    fun provideShopsViewModelFactory(context: Context): ShopsViewModelFactory {
        return ShopsViewModelFactory(getShopsRepository(context))
    }
}
