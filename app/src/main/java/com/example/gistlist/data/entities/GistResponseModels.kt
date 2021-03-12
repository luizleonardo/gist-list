package com.example.gistlist.data.entities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type


data class GistResponseHolder(val data: List<GistItem>)

data class GistItem(
    var id: String? = null,
    var url: String? = null,
    var description: String? = null,
    val owner: GistOwner? = null,
    val files: GistFileList? = null,
    var isFavorite: Boolean = false
)

data class GistOwner(
    val login: String? = null,
    var id: String? = null,
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
)

data class GistFileList(val fileList: List<GistFile>)

data class GistFile(val filename: String? = null, val type: String? = null)

class GistFileDeserializer : JsonDeserializer<GistFileList?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement,
        type: Type?,
        context: JsonDeserializationContext
    ): GistFileList {
        val jsonObject = jsonElement.asJsonObject
        val gistFiles: MutableList<GistFile> = ArrayList()
        jsonObject.entrySet().forEach {
            gistFiles.add(context.deserialize(it.value, GistFile::class.java))
        }
        return GistFileList(gistFiles)
    }
}