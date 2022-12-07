package com.assessment.exchangecurrency.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.assessment.exchangecurrency.data.ConversionRatesEntity

@Database(entities = [ConversionRatesEntity::class], version = 1)
abstract class CurrencyRatesDatabase : RoomDatabase() {

    abstract fun getConversionRatesDAO() : ConversionRatesDAO

    companion object {

        @Volatile
        private var INSTANCE : CurrencyRatesDatabase? = null
        fun getInstance(context : Context) : CurrencyRatesDatabase{
            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, CurrencyRatesDatabase::class.java, "currency_rates_database").build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}