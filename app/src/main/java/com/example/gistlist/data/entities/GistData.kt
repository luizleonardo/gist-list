package com.example.gistlist.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favorites")
data class GistData(
    @PrimaryKey
    val id: String = "",
    @ColumnInfo(name = "url")
    val url: String? = null,
    @ColumnInfo(name = "description")
    var description: String? = null,
    @Embedded
    val owner: OwnerData? = null,
    @ColumnInfo(name = "file_type")
    val fileType: String? = null,
    @ColumnInfo(name = "file_name")
    val fileName: String? = null
)

data class OwnerData(
    @ColumnInfo(name = "owner_login")
    val login: String? = null,
    @ColumnInfo(name = "owner_id")
    var id: String? = null,
    @ColumnInfo(name = "owner_avatar_url")
    var avatarUrl: String? = null,
)