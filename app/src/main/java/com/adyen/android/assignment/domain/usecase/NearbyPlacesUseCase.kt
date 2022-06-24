package com.adyen.android.assignment.domain.usecase

import com.adyen.android.assignment.domain.di.IoDispatcher
import com.adyen.android.assignment.domain.model.Location
import com.adyen.android.assignment.domain.model.Place
import com.adyen.android.assignment.domain.model.Result
import com.adyen.android.assignment.domain.repositories.NearbyPlacesRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class NearbyPlacesUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val nearbyPlacesRepository: NearbyPlacesRepository
) : @JvmSuppressWildcards AbstractUseCase<Location, List<Place>>(coroutineDispatcher) {
    override suspend fun execute(location: Location): Result<List<Place>> {
        return nearbyPlacesRepository.getNearbyPlaces(location)
    }
}