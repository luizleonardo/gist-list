package com.example.gistlist.data.repository

import com.example.gistlist.data.api.RemoteGithubApi
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.data.entities.GistResponseHolder
import io.reactivex.Observable

class GistRepository(private val apiService: RemoteGithubApi) {

    fun fetchPublicGists(perPage: Int?, page: Int?): Observable<List<GistItem>> =
        apiService.getPublicGists(perPage, page)

    fun search(limit: Int?, offset: Int?, query: String?): Observable<GistResponseHolder> =
        apiService.searchGifsByQuery(limit, offset, query)
}