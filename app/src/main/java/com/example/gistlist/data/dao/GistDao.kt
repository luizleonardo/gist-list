package com.example.gistlist.data.dao

import androidx.room.*
import com.example.gistlist.data.entities.GistData
import io.reactivex.Flowable

@Dao
interface GistDao {

    @Query("SELECT * FROM Favorites")
    fun findAll(): Flowable<List<GistData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(gistItem: GistData)

    @Delete
    fun delete(gistItem: GistData)
}