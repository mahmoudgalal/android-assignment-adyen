package com.adyen.android.assignment.di

import android.content.Context
import com.adyen.android.assignment.AndroidLoggerImpl
import com.adyen.android.assignment.BuildConfig
import com.adyen.android.assignment.data.api.PlacesService
import com.adyen.android.assignment.data.mappers.NetworkToDomainMapper
import com.adyen.android.assignment.data.model.ResponseWrapper
import com.adyen.android.assignment.data.repositories.NearbyPlacesRepositoryImpl
import com.adyen.android.assignment.domain.Logger
import com.adyen.android.assignment.domain.di.IoDispatcher
import com.adyen.android.assignment.domain.model.Location
import com.adyen.android.assignment.domain.model.Place
import com.adyen.android.assignment.domain.repositories.NearbyPlacesRepository
import com.adyen.android.assignment.domain.usecase.AbstractUseCase
import com.adyen.android.assignment.domain.usecase.LocationProvider
import com.adyen.android.assignment.domain.usecase.NearbyPlacesUseCase
import com.adyen.android.assignment.location.AndroidLocationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Modules {

    @Provides
    @Singleton
    fun providePlacesService(): PlacesService {
        val builder = Retrofit.Builder()
            .baseUrl(BuildConfig.FOURSQUARE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return builder.create(PlacesService::class.java)
    }

    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideNearbyPlacesUseCase(
        @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
        nearbyPlacesRepository: NearbyPlacesRepository
    ): AbstractUseCase<Location, List<Place>> =
        NearbyPlacesUseCase(coroutineDispatcher, nearbyPlacesRepository)


    @Provides
    @Singleton
    fun provideNearbyPlacesRepository(
        service: PlacesService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        mapper: NetworkToDomainMapper<ResponseWrapper, List<Place>>
    ): NearbyPlacesRepository {
        return NearbyPlacesRepositoryImpl(service, ioDispatcher, mapper)
    }

    @Provides
    @Singleton
    fun provideLocationProvider(
        @ApplicationContext context: Context
    ): LocationProvider {
        return {
            AndroidLocationProvider(
                context
            ).fetchUpdates()
        }
    }

    @Provides
    @Singleton
    fun provideLogger(): Logger = AndroidLoggerImpl()
}
