package br.com.brunocheles.mycontab.model.data.modules

import br.com.brunocheles.mycontab.model.di.ResourceProvider
import br.com.brunocheles.mycontab.model.di.ResourceProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        resourceProviderImpl: ResourceProviderImpl
    ): ResourceProvider
}