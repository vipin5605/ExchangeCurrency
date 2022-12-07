package com.assessment.exchangecurrency.dependency

import android.app.Application
import android.content.Context
import com.assessment.exchangecurrency.data.ExchangeCurrencyApi
import com.assessment.exchangecurrency.db.ConversionRatesDAO
import com.assessment.exchangecurrency.db.CurrencyRatesDatabase
import com.assessment.exchangecurrency.db.datasource.CurrencyLocalDataStorage
import com.assessment.exchangecurrency.db.datasourceimpl.CurrencyLocalDataStorageImpl
import com.assessment.exchangecurrency.repo.DefaultRepo
import com.assessment.exchangecurrency.repo.MainRepo
import com.assessment.exchangecurrency.repo.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideCurrencyApi(): ExchangeCurrencyApi = Retrofit.Builder()
        .baseUrl("https://v6.exchangerate-api.com/")
        .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ExchangeCurrencyApi::class.java)


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): CurrencyRatesDatabase {
        return CurrencyRatesDatabase.getInstance(appContext)
    }
    @Singleton
    @Provides
    fun provideDao(appDatabase: CurrencyRatesDatabase): ConversionRatesDAO {
        return appDatabase.getConversionRatesDAO()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(dao: ConversionRatesDAO): CurrencyLocalDataStorage {
        return CurrencyLocalDataStorageImpl(dao)
    }

    @Singleton
    @Provides
    fun provideMainRepository(
        context: Context,
        remoteDataSource: ExchangeCurrencyApi,
        localDataSource: CurrencyLocalDataStorage,
    ): MainRepo {
        return DefaultRepo(
            context,
            remoteDataSource,
            localDataSource
        )
    }

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }


}