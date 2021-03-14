package com.testm.demosdk.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.testm.demosdk.network.AudioFilesNetworkService
import com.testm.demosdk.repository.AudioFilesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * A dependency injection provider for the demo's different components
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl("https://dummy.url/") //Since we only fetch a full url address at runtime, we can workaround retrofit's request for a base url
        .build()

    @Singleton
    @Provides
    fun provideAudioFilesNetworkService(): AudioFilesNetworkService {
        return retrofit.create(AudioFilesNetworkService::class.java)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RepositoriesModule {

    @ViewModelScoped
    @Provides
    fun provideAudioFilesRepository(audioFilesNetworkService: AudioFilesNetworkService): AudioFilesRepository {
        return AudioFilesRepository(audioFilesNetworkService)
    }
}
