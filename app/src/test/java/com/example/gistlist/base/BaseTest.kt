package com.example.gistlist.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import io.mockk.unmockkAll
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit

abstract class BaseTest {
    protected val gson = Gson()
    protected val mockWebServer = MockWebServer()
    protected val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .dispatcher(Dispatcher(object : AbstractExecutorService() {
            override fun shutdown() {
            }

            override fun isTerminated(): Boolean {
                return false
            }

            override fun isShutdown(): Boolean {
                return false
            }

            override fun shutdownNow(): List<Runnable>? {
                return null
            }

            @Throws(InterruptedException::class)
            override fun awaitTermination(l: Long, timeUnit: TimeUnit): Boolean {
                return false
            }

            override fun execute(runnable: Runnable) {
                runnable.run()
            }
        }))
        .addInterceptor(
            HttpLoggingInterceptor { message -> println("okHttpClient $message") }.setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
        )
        .writeTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .connectTimeout(3, TimeUnit.SECONDS)
        .build()

    protected val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/").toString())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    open fun before() {
    }

    @After
    open fun after() {
        unmockkAll()
    }

    fun enqueueResponse(responsePath: String? = null, code: Int = 200) {
        mockWebServer.enqueue(MockResponse().setBody(responsePath?.let { getJsonAsString(it) }
            ?: "dummyTest").setResponseCode(code))
    }

    fun enqueueAsyncResponses(vararg triple: Triple<String, String, Int?>) {
        mockWebServer.dispatcher = object : okhttp3.mockwebserver.Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                triple.asList().forEach {
                    if (request.path?.contains(it.first) == true) {
                        return MockResponse().setResponseCode(
                            it.third
                                ?: 200
                        ).setBody(getJsonAsString(it.second))
                    }
                }

                return MockResponse().setResponseCode(404).setBody("")
            }
        }
    }

    protected fun getJsonAsString(responsePath: String): String {
        val iStream: InputStream? = openResource(responsePath)
        return readFile(iStream)
    }

    protected fun readFile(inputStream: InputStream?): String {
        try {
            val builder = StringBuilder()
            val reader = InputStreamReader(inputStream!!, "UTF-8")
            reader.readLines().forEach {
                builder.append(it)
            }
            return builder.toString()
        } catch (e: IOException) {
            throw RuntimeException("Cannot read file from specified path", e)
        }
    }

    private fun openResource(filename: String): InputStream? {
        return javaClass.classLoader?.getResourceAsStream(filename)
    }

}