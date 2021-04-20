package com.example.gistlist

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.gistlist.base.BaseUnitTest
import com.example.gistlist.data.dao.GistDatabase
import com.example.gistlist.data.entities.*
import com.example.gistlist.data.repository.RoomRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class RoomRepositoryTest : BaseUnitTest() {
    private lateinit var roomRepository: RoomRepository
    private lateinit var dataBase: GistDatabase

    private var gistItem: GistItem = GistItem()
    private var gistData: GistData = GistData()

    fun buildGistItemDummy(id: String = "0") = GistItem(
        id = id,
        url = "http://www.google.com",
        description = "o sub zero brasileiro",
        owner = GistOwner(
            login = "lindomar",
            id = "1234",
            avatarUrl = "http://www.google.com"
        ),
        files = GistFileList(listOf(GistFile(filename = "lindomar", type = "text"))),
        isFavorite = true
    )

    fun buildGistDataDummy(id: String = "0") = GistData(
        id = id,
        url = "http://www.google.com",
        description = "o sub zero brasileiro",
        owner = OwnerData(
            login = "lindomar",
            id = "1234",
            avatarUrl = "http://www.google.com"
        ),
        fileType = "text",
        fileName = "lindomar"
    )

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        dataBase =
            Room.inMemoryDatabaseBuilder(context, GistDatabase::class.java).allowMainThreadQueries()
                .build()
        roomRepository = RoomRepository(dataBase.gistDao)
        gistItem = buildGistItemDummy()
        gistData = buildGistDataDummy()
    }

    @After
    fun closeDb() {
        dataBase.close()
    }

    @Test
    fun gistItemIsSavedTest() {
        roomRepository.addFavorite(gistItem).subscribe()
        roomRepository.getFavorites().test()
            .assertValue { favorites ->
                favorites.size == 1
            }
    }

    @Test
    fun gistItemIsSavedOverwriteTest() {
        roomRepository.addFavorite(gistItem)
            .subscribe()
        roomRepository.addFavorite(gistItem)
            .subscribe()
        roomRepository.addFavorite(gistItem)
            .subscribe()
        roomRepository.getFavorites().test()
            .assertValue { favorites ->
                favorites.size == 1
            }
    }

    @Test
    fun loadAllGistItemTest() {
        roomRepository.addFavorite(buildGistItemDummy("23"))
            .subscribe()
        roomRepository.addFavorite(buildGistItemDummy("34"))
            .subscribe()
        roomRepository.addFavorite(buildGistItemDummy("45"))
            .subscribe()
        roomRepository.getFavorites().test()
            .assertValue { favorites ->
                favorites.size == 3
            }
    }

    @Test
    fun deleteGistItemTest() {
        roomRepository.addFavorite(gistItem)
            .subscribe()
        roomRepository.getFavorites().test()
            .assertValue { favorites ->
                favorites.size == 1
            }
        roomRepository.removeFavorite(gistItem)
            .subscribe()

        roomRepository.getFavorites().test()
            .assertValue { favorites ->
                favorites.isNullOrEmpty()
            }
    }

    @Test
    fun updateGistItemTest() {
        roomRepository.addFavorite(gistItem)
            .subscribe()
        roomRepository.addFavorite(gistItem.also {
            it.url = "http://www.youtube.com"
        })
            .subscribe()
        roomRepository.getFavorites().test()
            .assertValue { favorites ->
                favorites.size == 1
            }
    }

    @Test
    fun transformGistDataToGistItem() {
        val gistData1 = buildGistDataDummy("1")
        val gistData2 = buildGistDataDummy("2")
        val gistData3 = buildGistDataDummy("3")

        val gistItemList =
            roomRepository.transformGistDataToGistItem(
                listOf(
                    gistData1,
                    gistData2,
                    gistData3
                )
            )

        assert(gistItemList.size == 3)
        assert(gistItemList.filter { gistItem -> gistItem.id == gistData1.id }.size == 1)
        assert(gistItemList.filter { gistItem -> gistItem.id == gistData2.id }.size == 1)
        assert(gistItemList.filter { gistItem -> gistItem.id == gistData3.id }.size == 1)

    }

    @Test
    fun transformGistItemToGistData() {
        val gistData =
            roomRepository.transformGistItemToGistData(buildGistItemDummy())

        assert(gistData.id == this.gistData.id)
    }
}