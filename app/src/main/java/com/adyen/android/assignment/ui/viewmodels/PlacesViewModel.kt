package com.adyen.android.assignment.ui.viewmodels

import androidx.lifecycle.*
import com.adyen.android.assignment.domain.Logger
import com.adyen.android.assignment.domain.model.Location
import com.adyen.android.assignment.domain.model.Place
import com.adyen.android.assignment.domain.model.Result
import com.adyen.android.assignment.domain.usecase.AbstractUseCase
import com.adyen.android.assignment.domain.usecase.LocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val nearbyPlacesUseCase: @JvmSuppressWildcards AbstractUseCase<Location, List<Place>>,
    private val locationUseCase: LocationUseCase,
    private val logger: Logger
) : ViewModel() {

    lateinit var associatedLifeCycle: Lifecycle

    private val _allPlaces: MutableLiveData<List<Place>> = MutableLiveData()
    val allPlaces: LiveData<List<Place>>
        get() = _allPlaces

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String>
        get() = _error

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean>
        get() = _loading

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _error.value = throwable.message
        _loading.value = false
        logger.log(TAG, "Error :${throwable.message}")
    }

    fun loadNearbyPlaces() {
        viewModelScope.launch(exceptionHandler) {
            _loading.value = true
            locationUseCase.fetchLocation().flowWithLifecycle(associatedLifeCycle)
                .map {
                    logger.log(TAG, "Location:$it")
                    nearbyPlacesUseCase(it)
                }.collect {
                _loading.value = false
                logger.log(TAG, "API Result:$it")
                when (it) {
                    is Result.Success<List<Place>> -> _allPlaces.value = it.data
                    is Result.Error -> _error.value = it.exception.message
                }
            }
        }
    }

    companion object {
        private const val TAG = "ViewModel"
    }
}