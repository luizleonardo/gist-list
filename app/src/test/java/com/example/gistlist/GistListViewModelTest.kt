package com.example.gistlist

import android.app.Application
import com.example.gistlist.base.BaseUnitTest
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.data.repository.GistRepository
import com.example.gistlist.data.repository.RoomRepository
import com.example.gistlist.ui.gistList.GistListViewModel
import com.example.gistlist.ui.helper.ViewData
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import org.junit.Before
import org.junit.Test
import org.robolectric.annotation.Config

@Config(sdk = [21], application = Application::class)
class GistListViewModelTest : BaseUnitTest() {
    private val giphyRepositoryMockk: GistRepository = mockk(relaxed = true)
    private val roomRepositoryMockk: RoomRepository = mockk(relaxed = true)

    private lateinit var gistListViewModel: GistListViewModel

    @Before
    fun setup() {
        unmockkAll()
        mockkObject(MyApplication)

        gistListViewModel =
            GistListViewModel(giphyRepositoryMockk, roomRepositoryMockk)
    }

    @Test
    fun whenFetchPublicGistsStatusIsComplete() {
        val dataDummy: List<GistItem> = mockk(relaxed = true)
        val observableSpyk1: Observable<List<GistItem>> =
            spyk()
        val observableSpyk2: Observable<List<GistItem>> =
            spyk(Observable.create {
                it.onNext(dataDummy)
                it.onComplete()
            })

        every {
            giphyRepositoryMockk.fetchPublicGists(15, 0)
        } returns observableSpyk1

        every {
            observableSpyk1.flatMap(
                any<Function<List<GistItem>, Observable<List<GistItem>>>>(),
                any<BiFunction<List<GistItem>, List<GistItem>, List<GistItem>>>()
            )
        } returns observableSpyk2

        gistListViewModel.fetchPublicGists()

        assert(gistListViewModel.liveDataGists.value?.status == ViewData.Status.COMPLETE)
        assert(gistListViewModel.liveDataGists.value?.data == dataDummy)
    }

    @Test
    fun whenFetchPublicGistsStatusIsSuccess() {
        val dataDummy: List<GistItem> = mockk(relaxed = true)
        val observableSpyk1: Observable<List<GistItem>> =
            spyk()
        val observableSpyk2: Observable<List<GistItem>> =
            spyk(Observable.create {
                it.onNext(dataDummy)
            })

        every {
            giphyRepositoryMockk.fetchPublicGists(15, 0)
        } returns observableSpyk1

        every {
            observableSpyk1.flatMap(
                any<Function<List<GistItem>, Observable<List<GistItem>>>>(),
                any<BiFunction<List<GistItem>, List<GistItem>, List<GistItem>>>()
            )
        } returns observableSpyk2

        gistListViewModel.fetchPublicGists()

        assert(gistListViewModel.liveDataGists.value?.status == ViewData.Status.SUCCESS)
        assert(gistListViewModel.liveDataGists.value?.data == dataDummy)
    }

    @Test
    fun whenFetchPublicGistsStatusIsLoading() {
        every {
            giphyRepositoryMockk.fetchPublicGists(15, 0)
        } returns Observable.never()

        gistListViewModel.fetchPublicGists()

        assert(gistListViewModel.liveDataGists.value?.status == ViewData.Status.LOADING)
    }

    @Test
    fun whenFetchPublicGistsStatusIsError() {
        val observableSpyk1: Observable<List<GistItem>> = spyk()
        val observableSpyk2: Observable<List<GistItem>> = spyk(Observable.create {
            it.onError(Throwable(message = "error!"))
        })

        every {
            giphyRepositoryMockk.fetchPublicGists(15, 0)
        } returns observableSpyk1

        every {
            observableSpyk1.flatMap(
                any<Function<List<GistItem>, Observable<List<GistItem>>>>(),
                any<BiFunction<List<GistItem>, List<GistItem>, List<GistItem>>>()
            )
        } returns observableSpyk2

        gistListViewModel.fetchPublicGists()

        assert(gistListViewModel.liveDataGists.value?.status == ViewData.Status.ERROR)
        assert(gistListViewModel.liveDataGists.value?.error?.message == "error!")
    }

    @Test
    fun whenFetchPublicGistsDataIsEmpty() {
        val dataDummy: List<GistItem> = emptyList()
        val observableSpyk1: Observable<List<GistItem>> = spyk()
        val observableSpyk2: Observable<List<GistItem>> = spyk(Observable.create {
            it.onNext(dataDummy)
            it.onComplete()
        })

        every {
            giphyRepositoryMockk.fetchPublicGists(15, 0)
        } returns observableSpyk1

        every {
            observableSpyk1.flatMap(
                any<Function<List<GistItem>, Observable<List<GistItem>>>>(),
                any<BiFunction<List<GistItem>, List<GistItem>, List<GistItem>>>()
            )
        } returns observableSpyk2

        gistListViewModel.fetchPublicGists()

        assert(gistListViewModel.liveDataGists.value?.data?.isEmpty() == true)
    }

    @Test
    fun whenSearchStatusIsComplete() {
        val dataDummy: List<GistItem> = mockk(relaxed = true)
        val observableSpyk1: Observable<List<GistItem>> =
            spyk()
        val observableSpyk2: Observable<List<GistItem>> =
            spyk(Observable.create {
                it.onNext(dataDummy)
                it.onComplete()
            })

        every {
            giphyRepositoryMockk.search("username", 15, 0)
        } returns observableSpyk1

        every {
            observableSpyk1.flatMap(
                any<Function<List<GistItem>, Observable<List<GistItem>>>>(),
                any<BiFunction<List<GistItem>, List<GistItem>, List<GistItem>>>()
            )
        } returns observableSpyk2

        gistListViewModel.searchByUsername("username")

        assert(gistListViewModel.liveDataSearch.value?.status == ViewData.Status.COMPLETE)
        assert(gistListViewModel.liveDataSearch.value?.data == dataDummy)
    }

    @Test
    fun whenSearchStatusIsSuccess() {
        val dataDummy: List<GistItem> = mockk(relaxed = true)
        val observableSpyk1: Observable<List<GistItem>> =
            spyk()
        val observableSpyk2: Observable<List<GistItem>> =
            spyk(Observable.create {
                it.onNext(dataDummy)
            })

        every {
            giphyRepositoryMockk.search("username", 15, 0)
        } returns observableSpyk1

        every {
            observableSpyk1.flatMap(
                any<Function<List<GistItem>, Observable<List<GistItem>>>>(),
                any<BiFunction<List<GistItem>, List<GistItem>, List<GistItem>>>()
            )
        } returns observableSpyk2

        gistListViewModel.searchByUsername("username")

        assert(gistListViewModel.liveDataSearch.value?.status == ViewData.Status.SUCCESS)
        assert(gistListViewModel.liveDataSearch.value?.data == dataDummy)
    }

    @Test
    fun whenSearchStatusIsLoading() {
        every {
            giphyRepositoryMockk.search("username", 15, 0)
        } returns Observable.never()

        gistListViewModel.searchByUsername("username")

        assert(gistListViewModel.liveDataSearch.value?.status == ViewData.Status.LOADING)
    }

    @Test
    fun whenSearchStatusIsError() {
        val observableSpyk1: Observable<List<GistItem>> = spyk()
        val observableSpyk2: Observable<List<GistItem>> = spyk(Observable.create {
            it.onError(Throwable(message = "error!"))
        })

        every {
            giphyRepositoryMockk.search("username", 15, 0)
        } returns observableSpyk1

        every {
            observableSpyk1.flatMap(
                any<Function<List<GistItem>, Observable<List<GistItem>>>>(),
                any<BiFunction<List<GistItem>, List<GistItem>, List<GistItem>>>()
            )
        } returns observableSpyk2

        gistListViewModel.searchByUsername("username")

        assert(gistListViewModel.liveDataSearch.value?.status == ViewData.Status.ERROR)
        assert(gistListViewModel.liveDataSearch.value?.error?.message == "error!")
    }

    @Test
    fun whenSearchDataIsEmpty() {
        val dataDummy: List<GistItem> = emptyList()
        val observableSpyk1: Observable<List<GistItem>> = spyk()
        val observableSpyk2: Observable<List<GistItem>> = spyk(Observable.create {
            it.onNext(dataDummy)
            it.onComplete()
        })

        every {
            giphyRepositoryMockk.search("username", 15, 0)
        } returns observableSpyk1

        every {
            observableSpyk1.flatMap(
                any<Function<List<GistItem>, Observable<List<GistItem>>>>(),
                any<BiFunction<List<GistItem>, List<GistItem>, List<GistItem>>>()
            )
        } returns observableSpyk2

        gistListViewModel.searchByUsername("username")

        assert(gistListViewModel.liveDataSearch.value?.data?.isEmpty() == true)
    }

}