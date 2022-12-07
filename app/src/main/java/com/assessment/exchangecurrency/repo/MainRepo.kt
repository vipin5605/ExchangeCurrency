package com.assessment.exchangecurrency.repo

import com.assessment.exchangecurrency.data.ExchangeCurrencyResponse
import com.assessment.exchangecurrency.db.datasource.CurrencyLocalDataStorage
import com.assessment.exchangecurrency.repo.utils.Resource

interface MainRepo : CurrencyLocalDataStorage{
    suspend fun getRates(base: String) : Resource<ExchangeCurrencyResponse>
    suspend fun getCountriesFromRawFile():ArrayList<String>
    suspend fun isSyncRequired() : Boolean

}