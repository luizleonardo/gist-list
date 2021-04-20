package com.example.gistlist

import android.app.Application
import com.example.gistlist.base.BaseUnitTest
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.data.repository.RoomRepository
import com.example.gistlist.ui.favorites.FavoriteViewModel
import com.example.gistlist.ui.helper.ViewData
import io.mockk.*
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.robolectric.annotation.Config

@Config(sdk = [21], application = Application::class)
class FavoriteViewModelTest : BaseUnitTest() {
    private val roomRepositoryMockk: RoomRepository = mockk(relaxed = true)

    private lateinit var favoriteViewModel: FavoriteViewModel

    @Before
    fun setup() {
        unmockkAll()
        mockkObject(MyApplication)

        favoriteViewModel = FavoriteViewModel(roomRepositoryMockk)
    }

    @Test
    fun whenAddFavoriteStatusIsSuccess() {
        val dataDummy: GistItem = mockk(relaxed = true)

        every {
            roomRepositoryMockk.addFavorite(any())
        } returns Observable.just(Unit)

        favoriteViewModel.addFavorite(dataDummy)

        assert(favoriteViewModel.liveDataAddFavorite.value?.status == ViewData.Status.SUCCESS)
        assert(favoriteViewModel.liveDataAddFavorite.value?.data == "Added to favorite")
    }

    @Test
    fun whenAddFavoriteStatusIsLoading() {
        val dataDummy: GistItem = mockk(relaxed = true)

        every {
            roomRepositoryMockk.addFavorite(any())
        } returns Observable.never()

        favoriteViewModel.addFavorite(dataDummy)

        assert(favoriteViewModel.liveDataAddFavorite.value?.status == ViewData.Status.LOADING)
    }

    @Test
    fun whenAddFavoriteStatusIsError() {
        val dataDummy: GistItem = mockk(relaxed = true)

        every {
            roomRepositoryMockk.addFavorite(any())
        } returns spyk(Observable.create {
            it.onError(Throwable(message = "error!"))
        })

        favoriteViewModel.addFavorite(dataDummy)

        assert(favoriteViewModel.liveDataAddFavorite.value?.status == ViewData.Status.ERROR)
        assert(favoriteViewModel.liveDataAddFavorite.value?.error?.message == "error!")
    }

    @Test
    fun whenFavoriteListStatusIsComplete() {
        val dataDummy: List<GistItem> = mockk(relaxed = true)

        every {
            roomRepositoryMockk.getFavorites()
        } returns Observable.just(dataDummy)

        favoriteViewModel.getFavorites()

        assert(favoriteViewModel.liveDataFavoritesList.value?.status == ViewData.Status.COMPLETE)
        assert(favoriteViewModel.liveDataFavoritesList.value?.data == dataDummy)
    }

    @Test
    fun whenFavoriteListStatusIsLoading() {
        every {
            roomRepositoryMockk.getFavorites()
        } returns Observable.never()

        favoriteViewModel.getFavorites()

        assert(favoriteViewModel.liveDataFavoritesList.value?.status == ViewData.Status.LOADING)
    }

    @Test
    fun whenFavoriteListStatusIsError() {
        every {
            roomRepositoryMockk.getFavorites()
        } returns spyk(Observable.create {
            it.onError(Throwable(message = "error!"))
        })

        favoriteViewModel.getFavorites()

        assert(favoriteViewModel.liveDataFavoritesList.value?.status == ViewData.Status.ERROR)
        assert(favoriteViewModel.liveDataFavoritesList.value?.error?.message == "error!")
    }
}