package com.example.gistlist.data.repository

import com.example.gistlist.data.api.RemoteGithubApi
import com.example.gistlist.data.entities.GistItem
import io.reactivex.Observable

class GistRepository(private val apiService: RemoteGithubApi) {

    fun fetchPublicGists(perPage: Int = 15, page: Int = 0): Observable<List<GistItem>> =
        apiService.getPublicGists(perPage, page)

    fun search(username: String?, perPage: Int = 15, page: Int = 0): Observable<List<GistItem>> =
        apiService.searchGistByUsername(username, perPage, page)
}