package com.example.gistlist.ui.favorites

import androidx.lifecycle.LifecycleObserver
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.data.repository.RoomRepository
import com.example.gistlist.ui.LiveEvent
import com.example.gistlist.ui.ViewData
import com.example.gistlist.ui.ViewData.Status.*
import com.example.gistlist.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class FavoriteViewModel(private val roomRepository: RoomRepository) : BaseViewModel(),
    LifecycleObserver {

    val liveDataAddFavorite = LiveEvent<ViewData<String>>()

    val liveDataFavoritesList = LiveEvent<ViewData<List<GistItem>>>()

    fun addFavorite(data: GistItem) {
        compositeDisposable.add(
            roomRepository.addFavorite(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    liveDataAddFavorite.value =
                        ViewData(LOADING, "Adding to favorite...")
                }
                .subscribe(
                    {
                        liveDataAddFavorite.value =
                            ViewData(status = SUCCESS, data = "Added to favorite")
                    }, {
                        liveDataAddFavorite.value = ViewData(ERROR, error = it)
                    }
                )
        )
    }

    fun removeFavorite(data: GistItem) {
        compositeDisposable.add(
            roomRepository.removeFavorite(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    liveDataAddFavorite.value =
                        ViewData(LOADING, "Removing from favorite...")
                }
                .subscribe(
                    {
                        liveDataAddFavorite.value = ViewData(
                            status = SUCCESS,
                            data = "Removed from favorite"
                        )
                    }, {
                        liveDataAddFavorite.value = ViewData(ERROR, error = it)
                    }
                )
        )
    }

    fun getFavorites() {
        compositeDisposable.add(
            roomRepository.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    liveDataFavoritesList.value =
                        ViewData(LOADING)
                }
                .subscribeWith(object : DisposableObserver<List<GistItem>>() {
                    override fun onError(error: Throwable) {
                        liveDataFavoritesList.value = ViewData(ERROR, error = error)
                    }

                    override fun onComplete() {
                        liveDataFavoritesList.value =
                            ViewData(
                                status = COMPLETE,
                                data = liveDataFavoritesList.value?.data
                            )
                    }

                    override fun onNext(data: List<GistItem>) {
                        liveDataFavoritesList.value =
                            ViewData(status = SUCCESS, data = data)
                    }

                })
        )
    }
}