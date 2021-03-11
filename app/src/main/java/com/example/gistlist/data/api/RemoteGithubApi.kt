package com.example.gistlist.data.api

import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.data.entities.GistResponseHolder
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteGithubApi {

    @GET("gists/public")
    fun getPublicGists(
        @Query("per_page") perPage: Int? = 25,
        @Query("page") page: Int? = 0,
    ): Observable<List<GistItem>>

    @GET("search")
    fun searchGifsByQuery(
        @Query("limit") limit: Int? = 25,
        @Query("offset") offset: Int? = 0,
        @Query("q") query: String?
    ): Observable<GistResponseHolder>

}