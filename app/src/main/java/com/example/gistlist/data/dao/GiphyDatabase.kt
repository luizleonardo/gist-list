package com.example.gistlist.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gistlist.data.entities.GistData

@Database(
    entities = [GistData::class], version = 1, exportSchema = false
)

abstract class GistDatabase : RoomDatabase() {
    abstract val gistDao: GistDao
}