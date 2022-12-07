package com.assessment.exchangecurrency.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeCurrencyApi {
    @GET("v6/0835d21e0539612ab1841f46/latest/{base}")
    suspend fun getRates(
        @Path("base") base: String
    ) : Response<ExchangeCurrencyResponse>
}