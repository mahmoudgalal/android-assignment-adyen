package com.adyen.android.assignment.domain.model

data class Place(
    val id: String?,
    val name: String?,
    val description: String?,
    val website: String?,
    val photos: List<Photo>?
)
