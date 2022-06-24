package com.adyen.android.assignment.utils

import com.adyen.android.assignment.data.model.Result
import com.adyen.android.assignment.domain.model.Location

object Fixtures {
    val successfulNetworkResponse = listOf<Result>(
        Result(
            fsq_id = "1",
            categories = null,
            distance = 30,
            description = "",
            null,
            null,
            name = "First",
            null,
            null,
            null
        ),
        Result(
            fsq_id = "2",
            categories = null,
            distance = 30,
            description = "",
            null,
            null,
            name = "Second",
            null,
            null,
            null
        ),
        Result(
            fsq_id = "3",
            categories = null,
            distance = 30,
            description = "",
            null,
            null,
            name = "Third",
            null,
            null,
            null
        )
    )
    val reportedLocationList = listOf(
        Location(latitude=37.421998333333335, longitude=-122.084),
        Location(latitude=37.421998333353335, longitude=-123.084),
        Location(latitude=37.4219983333333225, longitude=-121.084)
    )
}