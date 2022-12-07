package com.assessment.exchangecurrency.data

import com.google.gson.annotations.SerializedName

data class ExchangeCurrencyResponse(
    @SerializedName("base_code")
    val baseCode: String,
    @SerializedName("conversion_rates")
    val result: Map<String, String>,
    @SerializedName("success")
    val success: Boolean
)
