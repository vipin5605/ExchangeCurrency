package com.assessment.exchangecurrency.db.datasource

import android.content.Context
import com.assessment.exchangecurrency.data.ConversionRatesEntity

interface CurrencyLocalDataStorage {

    suspend fun saveCurrenciesToDB(rates: List<ConversionRatesEntity>)
    suspend fun getSavedCurrencies(): List<ConversionRatesEntity>
    suspend fun getRateForSelectedCurrency(currency : String) : ConversionRatesEntity
}