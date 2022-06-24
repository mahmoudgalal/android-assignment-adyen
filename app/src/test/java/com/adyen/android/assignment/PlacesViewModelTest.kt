package com.adyen.android.assignment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adyen.android.assignment.data.api.PlacesService
import com.adyen.android.assignment.data.mappers.NetworkToDomainMapper
import com.adyen.android.assignment.data.model.ResponseWrapper
import com.adyen.android.assignment.data.repositories.NearbyPlacesRepositoryImpl
import com.adyen.android.assignment.domain.Logger
import com.adyen.android.assignment.domain.model.Place
import com.adyen.android.assignment.domain.repositories.NearbyPlacesRepository
import com.adyen.android.assignment.domain.usecase.LocationUseCase
import com.adyen.android.assignment.domain.usecase.NearbyPlacesUseCase
import com.adyen.android.assignment.ui.viewmodels.PlacesViewModel
import com.adyen.android.assignment.utils.Fixtures
import com.adyen.android.assignment.utils.getOrAwaitValue
import com.adyen.android.assignment.utils.observeForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class PlacesViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var placesViewModel: PlacesViewModel
    private val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private lateinit var nearbyPlacesRepository: NearbyPlacesRepository
    private lateinit var nearbyPlacesUseCase: NearbyPlacesUseCase
    private lateinit var placesService: PlacesService
    private lateinit var locationUseCase: LocationUseCase
    private val mapper = NetworkToDomainMapper<ResponseWrapper, List<Place>>()

    @Mock
    lateinit var logger: Logger
    private lateinit var closable: AutoCloseable

    @Before
    fun setupViewModel() {
        Dispatchers.setMain(testDispatcher)
        closable = MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test fetching all places successfully`() {
        runTest {
            placesService = mock {
                onBlocking { getVenueRecommendations(any()) } doSuspendableAnswer {
                    withContext(testDispatcher) {
                        Response.success(ResponseWrapper(Fixtures.successfulNetworkResponse))
                    }
                }
            }
            nearbyPlacesRepository =
                NearbyPlacesRepositoryImpl(placesService, testDispatcher, mapper)
            nearbyPlacesUseCase = NearbyPlacesUseCase(testDispatcher, nearbyPlacesRepository)
            locationUseCase = LocationUseCase {
                flow {
                    Fixtures.reportedLocationList.forEach {
                        emit(it)
                    }
                }
            }
            placesViewModel = PlacesViewModel(nearbyPlacesUseCase, locationUseCase, logger)
            //Action
            placesViewModel.loadNearbyPlaces()
            placesViewModel.allPlaces.observeForTesting {
                // Execute pending coroutines actions
                advanceUntilIdle()
                // Assert data correctly loaded
                with(placesViewModel.allPlaces.getOrAwaitValue()) {
                    assertThat(
                        "Not expected size",
                        size == Fixtures.reportedLocationList.size
                    )
                }
            }
        }
    }

    @Test
    fun `test error loading places`() {
        runTest {
            //Prepare
            placesService = mock {
                onBlocking { getVenueRecommendations(any()) } doSuspendableAnswer {
                    withContext(testDispatcher) {
                        Response.error(404, ResponseBody.create(MediaType.get("text/plain"), ""))
                    }
                }
            }
            nearbyPlacesRepository =
                NearbyPlacesRepositoryImpl(placesService, testDispatcher, mapper)
            nearbyPlacesUseCase = NearbyPlacesUseCase(testDispatcher, nearbyPlacesRepository)
            locationUseCase = LocationUseCase {
                flow {
                    Fixtures.reportedLocationList.forEach {
                        emit(it)
                    }
                }
            }
            placesViewModel = PlacesViewModel(nearbyPlacesUseCase, locationUseCase, logger)
            // Action
            placesViewModel.loadNearbyPlaces()
            with(placesViewModel) {
                loading.observeForTesting {
                    // Execute pending coroutines actions
                    advanceUntilIdle()
                    // Assert data loading error
                    assertThat(
                        "Shouldn't be Loading",
                        !loading.getOrAwaitValue()
                    )
                }
                error.observeForTesting {
                    // Execute pending coroutines actions
                    advanceUntilIdle()
                    // Assert data loading error
                    assertThat(
                        "Not expected error",
                        error.getOrAwaitValue().isNotEmpty()
                    )
                }
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        closable.close()
    }
}