package com.example.gistlist.ui.gistList

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ext.scaleAnimation
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class GistListViewHolder(
    itemView: View,
    private val favoriteCallback: FavoriteCallback?,
    private val gistItemCallback: GistItemCallback?,
) : RecyclerView.ViewHolder(itemView) {

    private val content: MaterialCardView =
        itemView.findViewById(R.id.list_item_gist_card_view)
    private val ownerAvatar: ShapeableImageView =
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
        itemView.setOnClickListener {
            gistItemCallback?.onGistItemClick(data)
        }
        favoriteButton.setOnCheckedChangeListener(null)
        favoriteButton.isChecked = data.isFavorite
        favoriteButton.setOnCheckedChangeListener { button, checked ->
            button.scaleAnimation()
            if (checked) {
                favoriteCallback?.onFavoriteAdd(data)
                return@setOnCheckedChangeListener
            }
            favoriteCallback?.onFavoriteRemove(data)
            return@setOnCheckedChangeListener
        }
        Picasso.get()
            .load(data.owner?.avatarUrl)
            .fit()
            .into(ownerAvatar)
        ownerName.text = data.owner?.login
        data.files?.fileList?.firstOrNull()?.run {
            gistType.text = type
            fileName.text = filename
        }
    }

    interface FavoriteCallback {
        fun onFavoriteAdd(data: GistItem)
        fun onFavoriteRemove(data: GistItem)
    }

    interface GistItemCallback {
        fun onGistItemClick(data: GistItem)
    }

}