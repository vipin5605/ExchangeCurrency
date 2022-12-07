package com.assessment.exchangecurrency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assessment.exchangecurrency.data.ConversionRatesEntity
import com.assessment.exchangecurrency.repo.MainRepo
import com.assessment.exchangecurrency.repo.utils.DispatcherProvider
import com.assessment.exchangecurrency.repo.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val mainRepository: MainRepo,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class CurrencyEvent {
        class Success(val resultText: String): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        object Loading: CurrencyEvent()
        object Empty: CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)

    private var countries = MutableLiveData<ArrayList<String>>()

    private var sourceCurrency = MutableLiveData<Long>()
    private var targetCurrency = MutableLiveData<Long>()

    private var currencyFrom = MutableLiveData<String>()
    private var currencyTo = MutableLiveData<String>()

    private var conversionRatesEntity = MutableLiveData<ConversionRatesEntity?>()

    val conversion : MutableStateFlow<CurrencyEvent>
    get() = _conversion

    val sourceAmount : LiveData<Long>
    get() = sourceCurrency

    val targetAmount : LiveData<Long>
    get() = targetCurrency


    val sourceCurr : LiveData<String>
    get() = currencyFrom

    val targetCurr : LiveData<String>
        get() = currencyTo

    val ratesEntity : LiveData<ConversionRatesEntity?>
    get() = conversionRatesEntity

    val countriesList : LiveData<ArrayList<String>>
    get() = countries



    fun convert(amount: String, fromCurrency: String, toCurrency: String) {
        val fromAmount = amount.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }


        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading

            val requiresSync = mainRepository.isSyncRequired()
            val localData = mainRepository.getSavedCurrencies()
            if (requiresSync || localData == null || localData.isEmpty()) {

                when(val rateResponse = mainRepository.getRates(fromCurrency)) {
                    is Resource.Error<*> -> _conversion.value = CurrencyEvent.Failure(rateResponse.message!!)
                    is Resource.Success<*> -> {
                        val rate = getRateForCurrency(toCurrency)

                        if (rate == null) {
                            _conversion.value = CurrencyEvent.Failure("Currency not found")
                        } else {
                            val convertedCurrency = (fromAmount * rate.value * 100).roundToInt() / 100
                            _conversion.value = CurrencyEvent.Success(
                                "$fromAmount $fromCurrency = $convertedCurrency $toCurrency"
                            )

                            withContext(Dispatchers.Main) {
                                sourceCurrency.value = fromAmount.toLong()
                                targetCurrency.value = convertedCurrency.toLong()
                                currencyFrom.value = fromCurrency
                                currencyTo.value = toCurrency
                                conversionRatesEntity.value = rate
                            }

                        }
                    }
                }

            } else {

                val rate = getRateForCurrency(toCurrency)

                if (rate == null) {
                    _conversion.value = CurrencyEvent.Failure("Currency not found")
                } else {
                    val convertedCurrency = (fromAmount * rate.value * 100).roundToInt() / 100
                    _conversion.value = CurrencyEvent.Success(
                        "$fromAmount $fromCurrency = $convertedCurrency $toCurrency"
                    )

                    withContext(Dispatchers.Main) {
                        sourceCurrency.value = fromAmount.toLong()
                        targetCurrency.value = convertedCurrency.toLong()
                        currencyFrom.value = fromCurrency
                        currencyTo.value = toCurrency
                        conversionRatesEntity.value = rate
                    }

                }

            }
        }
    }

    private suspend fun getRateForCurrency(currency: String) : ConversionRatesEntity {
        return mainRepository.getRateForSelectedCurrency(currency)
    }


    fun getCountries() {
        viewModelScope.launch(dispatchers.io) {

            val listOfCountries = mainRepository.getCountriesFromRawFile()

            if (listOfCountries != null && listOfCountries.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    countries.value = listOfCountries
                }
            }
        }
    }


}