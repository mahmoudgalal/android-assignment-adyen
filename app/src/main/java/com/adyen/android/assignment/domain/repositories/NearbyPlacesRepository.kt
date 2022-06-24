package com.adyen.android.assignment.domain.repositories

import com.adyen.android.assignment.domain.model.Location
import com.adyen.android.assignment.domain.model.Place
import com.adyen.android.assignment.domain.model.Result

interface NearbyPlacesRepository {
    suspend fun getNearbyPlaces(location: Location): Result<List<Place>>
}