package com.example.gistlist.data.entities

import com.google.gson.annotations.SerializedName

data class GistResponseHolder(val data: List<GistItem>)

data class GistItem(
    var id: String? = null,
    var url: String? = null,
    var description: String? = null,
    val owner: GistOwner? = null,
    var isFavorite: Boolean = false
)

data class GistOwner(
    val login: String? = null,
    var id: String? = null,
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
)