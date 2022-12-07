package com.assessment.exchangecurrency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.assessment.exchangecurrency.repo.MainRepo
import com.assessment.exchangecurrency.repo.utils.DispatcherProvider
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class MainActivityViewModelTest {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var mainRepo : MainRepo
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        mainRepo = Mockito.mock(MainRepo::class.java)
        dispatcherProvider = Mockito.mock(DispatcherProvider::class.java)
        Mockito.`when`(dispatcherProvider.io).thenReturn(testDispatcher)
        Mockito.`when`(dispatcherProvider.main).thenReturn(testDispatcher)
        mainActivityViewModel = MainActivityViewModel(mainRepo, dispatcherProvider)

    }

    @Test
    fun convert_InvalidAmountTest() = runBlocking {
        mainActivityViewModel.convert("", "USD", "AED")
        Truth.assertThat(mainActivityViewModel.conversion.value).isInstanceOf(MainActivityViewModel.CurrencyEvent.Failure::class.java)
    }

    @Test
    fun getCountries_NonNull_Test() = runBlocking {
        var list = ArrayList<String>()
        list.add("USD")
        list.add("AED")
        Mockito.`when`(mainRepo.getCountriesFromRawFile()).thenReturn(list)
        mainActivityViewModel.getCountries()
        delay(1000)
        Truth.assertThat(mainActivityViewModel.countriesList).isNotNull()
    }
}