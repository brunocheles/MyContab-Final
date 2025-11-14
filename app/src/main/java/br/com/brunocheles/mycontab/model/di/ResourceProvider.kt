package br.com.brunocheles.mycontab.model.di

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
}

@Singleton
class ResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}