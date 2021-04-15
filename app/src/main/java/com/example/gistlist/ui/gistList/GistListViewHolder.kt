package com.example.gistlist.ui.gistList

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ext.scaleAnimation
import com.example.gistlist.ui.helper.CircleTransform
import com.squareup.picasso.Picasso

class GistListViewHolder(
    itemView: View,
    private val gistItemCallback: GistItemCallback?,
) : RecyclerView.ViewHolder(itemView) {

    private val ownerAvatar: AppCompatImageView =
        itemView.findViewById(R.id.list_item_gist_owner_avatar)
    private val ownerName: AppCompatTextView =
        itemView.findViewById(R.id.list_item_gist_owner_name)
    private val fileName: AppCompatTextView =
        itemView.findViewById(R.id.list_item_gist_filename)
    private val gistType: AppCompatTextView =
        itemView.findViewById(R.id.list_item_gist_type)
    private val favoriteButton: AppCompatToggleButton =
        itemView.findViewById(R.id.list_item_gist_button_favorite)

    fun bind(data: GistItem) {
        ViewCompat.setTransitionName(ownerAvatar, data.owner?.login)
        itemView.setOnClickListener {
            gistItemCallback?.onGistItemClick(data, ownerAvatar)
        }
        favoriteButton.setOnCheckedChangeListener(null)
        favoriteButton.isChecked = data.isFavorite
        favoriteButton.setOnCheckedChangeListener { button, checked ->
            button.scaleAnimation()
            if (checked) {
                gistItemCallback?.onFavoriteAdd(data)
                return@setOnCheckedChangeListener
            }
            gistItemCallback?.onFavoriteRemove(data)
            return@setOnCheckedChangeListener
        }
        Picasso.get()
            .load(data.owner?.avatarUrl)
            .placeholder(R.drawable.vector_broken_image)
            .error(R.drawable.vector_broken_image)
            .transform(CircleTransform())
            .fit()
            .into(ownerAvatar)
        ownerName.text = data.owner?.login
        data.files?.fileList?.firstOrNull()?.run {
            gistType.text = type
            fileName.text = filename
        }
    }

    interface GistItemCallback {
        fun onFavoriteAdd(data: GistItem)
        fun onFavoriteRemove(data: GistItem)
        fun onGistItemClick(data: GistItem, ownerAvatar: AppCompatImageView)
    }

}