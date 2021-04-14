package com.example.gistlist.data.api

import com.example.gistlist.data.entities.GistItem
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteGithubApi {

    @GET("gists/public")
    fun getPublicGists(
        @Query("per_page") perPage: Int = 25,
        @Query("page") page: Int = 0,
    ): Observable<List<GistItem>>

    @GET("/users/{username}/gists")
    fun searchGistByUsername(
        @Path("username") username: String?,
        @Query("per_page") perPage: Int = 25,
        @Query("page") page: Int = 0,
    ): Observable<List<GistItem>>

}