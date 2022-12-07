package com.assessment.exchangecurrency.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assessment.exchangecurrency.data.ConversionRatesEntity

@Dao
interface ConversionRatesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(conversionRatesEntities: List<ConversionRatesEntity>)

    @Query("DELETE FROM conversion_rates")
    suspend fun deleteAll()

    @Query("SELECT *  FROM conversion_rates")
    suspend fun getRatesFromLocal(): List<ConversionRatesEntity>

    @Query("SELECT *  FROM conversion_rates where currency_name = :currency")
    suspend fun getRatesFromCurrency(currency : String): ConversionRatesEntity
}