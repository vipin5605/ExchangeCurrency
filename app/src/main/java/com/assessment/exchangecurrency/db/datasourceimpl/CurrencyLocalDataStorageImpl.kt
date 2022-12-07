package com.assessment.exchangecurrency.db.datasourceimpl

import com.assessment.exchangecurrency.data.ConversionRatesEntity
import com.assessment.exchangecurrency.db.ConversionRatesDAO
import com.assessment.exchangecurrency.db.datasource.CurrencyLocalDataStorage

class CurrencyLocalDataStorageImpl(private val dao: ConversionRatesDAO) : CurrencyLocalDataStorage {
    override suspend fun saveCurrenciesToDB(rates: List<ConversionRatesEntity>) {

        dao.insertRates(rates)
    }

    override suspend fun getSavedCurrencies(): List<ConversionRatesEntity> {
        return dao.getRatesFromLocal()
    }

    override suspend fun getRateForSelectedCurrency(currency: String): ConversionRatesEntity {
        return dao.getRatesFromCurrency(currency)
    }
}