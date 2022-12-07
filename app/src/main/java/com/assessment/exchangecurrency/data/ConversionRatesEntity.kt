package com.assessment.exchangecurrency.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_rates")
data class ConversionRatesEntity(

    @PrimaryKey
    @ColumnInfo(name = "currency_name")
    val currency : String,

    @ColumnInfo(name = "currency_value")
    val value : Double
)
