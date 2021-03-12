package com.example.gistlist.ui.gistList

import android.view.View
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class GistListViewHolder(
    itemView: View,
    private val favoriteCallback: FavoriteCallback?
) : RecyclerView.ViewHolder(itemView) {

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

    private val scaleAnimation = ScaleAnimation(
        0.7f,
        1.0f,
        0.7f,
        1.0f,
        Animation.RELATIVE_TO_SELF,
        0.7f,
        Animation.RELATIVE_TO_SELF,
        0.7f
    ).also {
        it.duration = 500
        it.interpolator = BounceInterpolator()
    }

    fun bind(data: GistItem) {
        favoriteButton.setOnCheckedChangeListener(null)
        favoriteButton.isChecked = data.isFavorite
        favoriteButton.setOnCheckedChangeListener { button, checked ->
            button?.startAnimation(scaleAnimation)
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

}