package com.adyen.android.assignment.data.api

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class PlacesQueryBuilder {

    fun build(): Map<String, String> {
        val queryParams = hashMapOf("v" to dateFormat.format(Date()),
                                    "fields" to defaultFields.joinToString(separator = ","))
        putQueryParams(queryParams)
        return queryParams
    }

    abstract fun putQueryParams(queryParams: MutableMap<String, String>)

    companion object {
        private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
        private val defaultFields = listOf("fsq_id","name","description","website","photos")
    }
}
