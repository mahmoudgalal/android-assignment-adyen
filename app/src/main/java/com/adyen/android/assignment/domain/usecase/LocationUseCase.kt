package com.adyen.android.assignment.domain.usecase

import com.adyen.android.assignment.domain.model.Location
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias LocationProvider = () -> Flow<Location>

class LocationUseCase @Inject constructor(
    private val locationProvide: @JvmSuppressWildcards LocationProvider
) {
    fun fetchLocation(): Flow<Location> = locationProvide()
}