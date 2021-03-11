package com.example.gistlist.data.repository

import com.example.gistlist.data.dao.GistDao
import com.example.gistlist.data.entities.GistData
import com.example.gistlist.data.entities.GistItem
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RoomRepository(private val dao: GistDao) {

    fun addFavorite(data: GistItem): Observable<Unit> = Observable.fromCallable {
        dao.add(transformImageItemToImageData(data))
    }.subscribeOn(Schedulers.io())

    fun removeFavorite(data: GistItem): Observable<Unit> = Observable.fromCallable {
        dao.delete(transformImageItemToImageData(data))
    }.subscribeOn(Schedulers.io())

    fun getFavorites(): Observable<List<GistItem>> = dao.findAll()
        .toObservable()
        .map { return@map transformImageDataToImageItem(it) }
        .subscribeOn(Schedulers.io())

    fun updateGistItem(data: List<GistItem>): Observable<List<GistItem>> =
        Observable.create {
            dao.findAll()
                .map { gistEntity ->
                    data.forEach { gistItem ->
                        gistEntity.map {gistData ->
                            if (gistData.id == gistItem.id)
                                gistItem.isFavorite = true
                        }
                    }
                    it.onNext(data ?: emptyList())
                    it.onComplete()
                }
                .subscribe()
        }

    private fun transformImageItemToImageData(imageItem: GistItem): GistData =
        GistData(
            id = imageItem.id ?: "",
            url = imageItem.url,
        )

    private fun transformImageDataToImageItem(imageDataList: List<GistData>?) =
        imageDataList?.map {
            GistItem(
                id = it.id,
                url = it.url,
                isFavorite = true,
                owner = null
            )
        } ?: ArrayList()

}