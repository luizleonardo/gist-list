package com.example.gistlist.di

import com.example.gistlist.BuildConfig
import com.example.gistlist.data.entities.GistFileDeserializer
import com.example.gistlist.data.entities.GistFileList
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

private const val ACCEPT_HEADER_PARAMETER = "accept"
private const val ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json"

fun createNetworkClient(baseUrl: String) = retrofitClient(baseUrl, httpClient())

class BasicAuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest =
            request.newBuilder().addHeader(ACCEPT_HEADER_PARAMETER, ACCEPT_HEADER_VALUE).build()
        return chain.proceed(newRequest)
    }
}

private fun httpClient(): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()
    if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).let {
            it.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(it)
        }
    }
    clientBuilder.also {
        it.addInterceptor(BasicAuthInterceptor())
    }
    return clientBuilder.build()
}

val gson: Gson = GsonBuilder().registerTypeAdapter(GistFileList::class.java, GistFileDeserializer()).setLenient().create()

private fun retrofitClient(baseUrl: String, httpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()