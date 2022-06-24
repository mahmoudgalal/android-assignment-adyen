package com.adyen.android.assignment.data.repositories

import com.adyen.android.assignment.data.api.PlacesService
import com.adyen.android.assignment.data.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.data.mappers.NetworkToDomainMapper
import com.adyen.android.assignment.data.model.ResponseWrapper
import com.adyen.android.assignment.domain.di.IoDispatcher
import com.adyen.android.assignment.domain.model.Location
import com.adyen.android.assignment.domain.model.Photo
import com.adyen.android.assignment.domain.model.Place
import com.adyen.android.assignment.domain.repositories.NearbyPlacesRepository
import com.adyen.android.assignment.domain.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NearbyPlacesRepositoryImpl @Inject constructor(
    private val service: PlacesService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val mapper: NetworkToDomainMapper<ResponseWrapper, List<Place>>
) : NearbyPlacesRepository {
    override suspend fun getNearbyPlaces(location: Location): Result<List<Place>> =
        withContext(ioDispatcher) {
            val query = VenueRecommendationsQueryBuilder().setLatitudeLongitude(
                location.latitude,
                location.longitude
            ).build()
            val ret = service.getVenueRecommendations(query)
            mapper.map(ret) { res ->
                res.results?.map {
                    Place(id = it.fsq_id,
                        name = it.name,
                        description = it.description,
                        website = it.website,
                        photos = it.photos?.map {
                            Photo(id = it.id, url = it.prefix + "original" + it.suffix)
                        })
                } ?: listOf()
            }
        }
}