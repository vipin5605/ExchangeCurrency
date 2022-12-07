package com.assessment.exchangecurrency.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.assessment.exchangecurrency.R
import com.assessment.exchangecurrency.data.ConversionRatesEntity
import com.assessment.exchangecurrency.data.ExchangeCurrencyApi
import com.assessment.exchangecurrency.data.ExchangeCurrencyResponse
import com.assessment.exchangecurrency.db.datasource.CurrencyLocalDataStorage
import com.assessment.exchangecurrency.repo.utils.Resource
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.InputStream
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class DefaultRepo(
    private val context: Context,
    private val api: ExchangeCurrencyApi,
    private val localDataStorage: CurrencyLocalDataStorage,
) : MainRepo {

    private val MY_PREFS_NAME = "SHARED-DRIVE"
    private val LAST_SYNC_TIME = "last_sync_time"

    override suspend fun getRates(base: String): Resource<ExchangeCurrencyResponse> {
        return try {
            val response = api.getRates(base)
            val result = response.body()

            if (response.isSuccessful && result != null) {
                saveCurrencies(result.result)
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An error occured")
        }
    }

    private suspend fun saveCurrencies(rates: Map<String, String>) {

        var rateList = ArrayList<ConversionRatesEntity>()
        for (entry in rates.entries) {
            var  currency = ConversionRatesEntity(entry.key, entry.value.toDouble())
            rateList?.add(currency)
        }

        if (rateList != null) {
            saveCurrenciesToDB(rateList)

        }

    }

    override suspend fun saveCurrenciesToDB(rates: List<ConversionRatesEntity>) {
        localDataStorage.saveCurrenciesToDB(rates)
        saveLastSyncTime()
    }

    override suspend fun getSavedCurrencies(): List<ConversionRatesEntity> {
        return localDataStorage.getSavedCurrencies()
    }

    override suspend fun getRateForSelectedCurrency(currency: String): ConversionRatesEntity {
        return localDataStorage.getRateForSelectedCurrency(currency)
    }

    override suspend fun getCountriesFromRawFile(): ArrayList<String> {
        val items = ArrayList<String>()

        try {
            val iStream: InputStream = context.resources.openRawResource(R.raw.countries)
            val builder: DocumentBuilder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
            val doc: Document = builder.parse(iStream, null)
            val words: NodeList = doc.getElementsByTagName("code")
            for (i in 0 until words.length) {
                items.add((words.item(i) as Element).getAttribute("value"))
            }
            iStream.close()
        } catch (t: Throwable) {
        }

        return items
    }

    override suspend fun isSyncRequired(): Boolean {
        try {

            var currentMillis = System.currentTimeMillis()

            var lastSyncTime = getLastSyncTime()

            if (lastSyncTime <= 0) {
                return true
            }

            var syncIntrvl = getTimeConfig().toString().toLong()

            var diffInMillis = TimeUnit.HOURS.toMillis(syncIntrvl)

            if ((currentMillis - lastSyncTime) >= diffInMillis) {

                return true
            }

        } catch (e:Exception) {

        }

        return false
    }


    fun saveLastSyncTime() {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putLong(LAST_SYNC_TIME, System.currentTimeMillis())
        editor.apply()
    }

    fun getLastSyncTime() : Long {
        val prefs = context.getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        val lastSyncTime =
            prefs.getLong(LAST_SYNC_TIME, 0)

        return lastSyncTime
    }

    fun getTimeConfig(): String? {
        val items = ArrayList<String>()

        try {
            val iStream: InputStream = context.resources.openRawResource(R.raw.synctime)
            val builder: DocumentBuilder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
            val doc: Document = builder.parse(iStream, null)
            val words: NodeList = doc.getElementsByTagName("interval")
            for (i in 0 until words.length) {
                items.add((words.item(i) as Element).getAttribute("value"))
            }
            iStream.close()
        } catch (t: Throwable) {
        }

        return if (items != null && items.isNotEmpty()) {
            items[0]
        } else{
            null
        }
    }


}