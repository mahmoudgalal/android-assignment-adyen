package com.adyen.android.assignment.domain.model

data class Location(
    val latitude: Double,
    val longitude: Double
) {
    val ll: String by lazy {
        "$latitude:$longitude"
    }
}
