package com.adyen.android.assignment.data.mappers

import com.adyen.android.assignment.domain.model.Result
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

class NetworkToDomainMapper<T, U> @Inject constructor() {
    fun map(networkResponse: Response<T>, contentMapper: (T) -> U): Result<U>  = with(networkResponse){
        val body = body()
        return if (isSuccessful && body != null)
            Result.Success(contentMapper(body))
        else Result.Error(Exception(message() + ":${errorBody()?.string()}" ))
    }
}