package com.example.gistlist.ui.repos

import androidx.lifecycle.LifecycleObserver
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.data.repository.GistRepository
import com.example.gistlist.data.repository.RoomRepository
import com.example.gistlist.ui.LiveEvent
import com.example.gistlist.ui.ViewData
import com.example.gistlist.ui.ViewData.Status.*
import com.example.gistlist.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class GistListViewModel(
    private val giphyRepository: GistRepository,
    private val roomRepository: RoomRepository
) : BaseViewModel(), LifecycleObserver {

    val liveDataGists = LiveEvent<ViewData<List<GistItem>>>()

    val liveDataSearch = LiveEvent<ViewData<List<GistItem>>>()

    fun fetchPublicGists(limit: Int? = null, offset: Int? = null) {
        compositeDisposable.add(
            giphyRepository.fetchPublicGists(25, 0)
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

/*    fun search(limit: Int? = null, offset: Int? = null, query: String?) {
        compositeDisposable.add(
            giphyRepository.search(limit, offset, query)
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
    }*/

}