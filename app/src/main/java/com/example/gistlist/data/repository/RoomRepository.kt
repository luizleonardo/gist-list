package com.example.gistlist.data.repository

import com.example.gistlist.data.dao.GistDao
import com.example.gistlist.data.entities.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RoomRepository(private val dao: GistDao) {

    fun addFavorite(data: GistItem): Observable<Unit> = Observable.fromCallable {
        dao.add(transformGistItemToGistData(data))
    }.subscribeOn(Schedulers.io())

    fun removeFavorite(data: GistItem): Observable<Unit> = Observable.fromCallable {
        dao.delete(transformGistItemToGistData(data))
    }.subscribeOn(Schedulers.io())

    fun getFavorites(): Observable<List<GistItem>> = dao.findAll()
        .toObservable()
        .map { return@map transformGistDataToGistItem(it) }
        .subscribeOn(Schedulers.io())

    fun updateGistItem(data: List<GistItem>): Observable<List<GistItem>> =
        Observable.create {
            dao.findAll()
                .map { gistEntity ->
                    data.forEach { gistItem ->
                        gistEntity.map { gistData ->
                            if (gistData.id == gistItem.id)
                                gistItem.isFavorite = true
                        }
                    }
                    it.onNext(data)
                    it.onComplete()
                }
                .subscribe()
        }

    private fun transformGistItemToGistData(gistItem: GistItem): GistData =
        GistData(
            id = gistItem.id.orEmpty(),
            url = gistItem.url,
            description = gistItem.description,
            owner = OwnerData(
                login = gistItem.owner?.login,
                id = gistItem.owner?.id,
                avatarUrl = gistItem.owner?.avatarUrl
            ),
            fileType = gistItem.files?.fileList?.firstOrNull()?.type,
            fileName = gistItem.files?.fileList?.firstOrNull()?.filename
        )

    private fun transformGistDataToGistItem(imageDataList: List<GistData>?) =
        imageDataList?.map {
            GistItem(
                id = it.id,
                url = it.url,
                description = it.description,
                isFavorite = true,
                owner = GistOwner(
                    login = it.owner?.login,
                    id = it.owner?.id,
                    avatarUrl = it.owner?.avatarUrl
                ),
                files = GistFileList(listOf(GistFile(filename = it.fileName, type = it.fileType)))
            )
        } ?: ArrayList()
}