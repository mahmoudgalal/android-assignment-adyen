package com.adyen.android.assignment

import com.adyen.android.assignment.data.api.PlacesService
import com.adyen.android.assignment.data.api.VenueRecommendationsQueryBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PlacesUnitTest {
    @Test
    fun testResponseCode() {
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(52.376510, 4.905890)
            .build()
        val response = runBlocking {
            PlacesService.instance
                .getVenueRecommendations(query)
        }
        val errorBody = response.errorBody()
        assertNull("Received an error: ${errorBody?.string()}", errorBody)

        val responseWrapper = response.body()
        assertNotNull("Response is null.", responseWrapper)
        assertEquals("Response code", 200, response.code())
    }
}
