package com.assessment.exchangecurrency

import com.assessment.exchangecurrency.data.ExchangeCurrencyApi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExchangeCurrencyAPITest {

    private lateinit var service: ExchangeCurrencyApi
    private lateinit var server: MockWebServer


    @Before
    fun setUp() {
        server = MockWebServer()
        service =Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeCurrencyApi::class.java)
    }


    private fun enqueueMockResponse(
        fileName:String
    ){
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        mockResponse.setBody(source.readString(Charsets.UTF_8))
        server.enqueue(mockResponse)

    }

    @Test
    fun getExchangeCurrency_receivedResponse_correctValue(){
        runBlocking {
            enqueueMockResponse("currency_response.json")
            val responseBody = service.getRates("USD").body()
            val articlesList = responseBody!!.result
            val article = articlesList["AED"]
            assertThat(article).isEqualTo("3.6725")
        }
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

}