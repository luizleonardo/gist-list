package com.example.gistlist.ui.gistList

import androidx.lifecycle.LifecycleObserver
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.data.repository.GistRepository
import com.example.gistlist.data.repository.RoomRepository
import com.example.gistlist.ui.base.BaseViewModel
import com.example.gistlist.ui.helper.SingleLiveEvent
import com.example.gistlist.ui.helper.ViewData
import com.example.gistlist.ui.helper.ViewData.Status.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class GistListViewModel(
    private val giphyRepository: GistRepository,
    private val roomRepository: RoomRepository
) : BaseViewModel(), LifecycleObserver {

    companion object {
        const val DEFAULT_PER_PAGE = 15
        const val DEFAULT_PAGE_START = 0
    }

    val liveDataGists = SingleLiveEvent<ViewData<List<GistItem>>>()

    val liveDataGistsPagination = SingleLiveEvent<ViewData<List<GistItem>>>()

    val liveDataSearch = SingleLiveEvent<ViewData<List<GistItem>>>()

    val liveDataSearchPagination = SingleLiveEvent<ViewData<List<GistItem>>>()

    fun fetchPublicGists() {
        compositeDisposable.add(
            giphyRepository.fetchPublicGists(DEFAULT_PER_PAGE, DEFAULT_PAGE_START)
                .flatMap(
                    {
                        return@flatMap roomRepository.updateGistItem(it)
                    },
                    { _: List<GistItem>, updatedList: List<GistItem> ->
                        updatedList
                    }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { liveDataGists.postValue(ViewData(LOADING)) }
                .subscribeWith(object : DisposableObserver<List<GistItem>>() {
                    override fun onNext(response: List<GistItem>) {
                        liveDataGists.value =
                            ViewData(status = SUCCESS, data = response)
                    }

                    override fun onError(error: Throwable) {
                        liveDataGists.value = ViewData(ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataGists.value =
                            ViewData(status = COMPLETE, data = liveDataGists.value?.data)
                    }

                })
        )
    }

    fun fetchPublicGistsPagination(
        perPage: Int = DEFAULT_PER_PAGE,
        page: Int = DEFAULT_PAGE_START
    ) {
        compositeDisposable.add(
            giphyRepository.fetchPublicGists(perPage, page)
                .flatMap(
                    {
                        return@flatMap roomRepository.updateGistItem(it)
                    },
                    { _: List<GistItem>, updatedList: List<GistItem> ->
                        updatedList
                    }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { liveDataGistsPagination.postValue(ViewData(LOADING)) }
                .subscribeWith(object : DisposableObserver<List<GistItem>>() {
                    override fun onNext(response: List<GistItem>) {
                        liveDataGistsPagination.value =
                            ViewData(status = SUCCESS, data = response)
                    }

                    override fun onError(error: Throwable) {
                        liveDataGistsPagination.value = ViewData(ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataGistsPagination.value =
                            ViewData(status = COMPLETE, data = liveDataGistsPagination.value?.data)
                    }

                })
        )
    }

    fun searchByUsername(username: String?) {
        compositeDisposable.add(
            giphyRepository.search(username, DEFAULT_PER_PAGE, DEFAULT_PAGE_START)
                .flatMap(
                    {
                        return@flatMap roomRepository.updateGistItem(it)
                    },
                    { _: List<GistItem>, updatedList: List<GistItem> ->
                        updatedList
                    }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { liveDataSearch.postValue(ViewData(LOADING)) }
                .subscribeWith(object : DisposableObserver<List<GistItem>>() {
                    override fun onNext(response: List<GistItem>) {
                        liveDataSearch.value =
                            ViewData(status = SUCCESS, data = response)
                    }

                    override fun onError(error: Throwable) {
                        liveDataSearch.value = ViewData(ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataSearch.value =
                            ViewData(status = COMPLETE, data = liveDataSearch.value?.data)
                    }

                })
        )
    }

    fun searchByUsernamePagination(
        username: String?,
        perPage: Int = DEFAULT_PER_PAGE,
        page: Int = DEFAULT_PAGE_START
    ) {
        compositeDisposable.add(
            giphyRepository.search(username, perPage, page)
                .flatMap(
                    {
                        return@flatMap roomRepository.updateGistItem(it)
                    },
                    { _: List<GistItem>, updatedList: List<GistItem> ->
                        updatedList
                    }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { liveDataSearchPagination.postValue(ViewData(LOADING)) }
                .subscribeWith(object : DisposableObserver<List<GistItem>>() {
                    override fun onNext(response: List<GistItem>) {
                        liveDataSearchPagination.value =
                            ViewData(status = SUCCESS, data = response)
                    }

                    override fun onError(error: Throwable) {
                        liveDataSearchPagination.value = ViewData(ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataSearchPagination.value =
                            ViewData(status = COMPLETE, data = liveDataSearchPagination.value?.data)
                    }
                })
        )
    }

}