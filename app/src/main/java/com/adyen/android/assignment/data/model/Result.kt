package com.adyen.android.assignment.data.model

data class Result(
    val fsq_id: String?,
    val categories: List<Category>?,
    val distance: Int?,
    val description: String?,
    val geocode: GeoCode?,
    val location: Location?,
    val name: String?,
    val timezone: String?,
    val website: String?,
    val photos: List<Photo>?
)