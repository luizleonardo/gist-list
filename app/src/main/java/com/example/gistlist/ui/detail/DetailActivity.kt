package com.example.gistlist.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ext.scaleAnimation
import com.example.gistlist.ui.base.BaseActivity
import com.example.gistlist.ui.favorites.FavoriteViewModel
import com.example.gistlist.ui.helper.ViewData
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.viewmodel.ext.android.viewModel

class DetailActivity : BaseActivity() {

    companion object {
        const val EXTRA_GIST_ITEM_VIEW = "gist_item_view_extra"
        const val EXTRA_GIST_ITEM = "gist_item_extra"
    }

    private var gistItem: GistItem? = null
    private var imageTransitionName: String? = null

    override fun layoutResource(): Int = R.layout.activity_detail

    private val favoriteViewModel: FavoriteViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.extras?.run {
            gistItem = this.getParcelable(EXTRA_GIST_ITEM)
            imageTransitionName = this.getString(EXTRA_GIST_ITEM_VIEW)
        }
        supportPostponeEnterTransition()
        super.onCreate(savedInstanceState)

        with(favoriteViewModel) {
            lifecycle.addObserver(this)
            observeFavorite(this)
        }
    }

    override fun setupView() {
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        gistItem?.run {
            activity_detail_owner_avatar.transitionName = imageTransitionName
            Picasso.get()
                .load(this.owner?.avatarUrl)
                .noFade()
                .into(activity_detail_owner_avatar, object : Callback {
                    override fun onSuccess() {
                        supportStartPostponedEnterTransition()
                    }

                    override fun onError(e: Exception?) {
                        supportStartPostponedEnterTransition()
                    }
                })
            activity_detail_description.text = this.description
            activity_detail_owner_name.text = this.owner?.login
            activity_detail_filename.text = this.files?.fileList?.firstOrNull()?.filename
            activity_detail_type.text = this.files?.fileList?.firstOrNull()?.type
            activity_detail_button_favorite.setOnCheckedChangeListener(null)
            activity_detail_button_favorite.isChecked = this.isFavorite
            activity_detail_button_favorite.setOnCheckedChangeListener { _, checked ->
                activity_detail_button_favorite.scaleAnimation()
                setResult(RESULT_OK)
                if (checked) {
                    favoriteViewModel.addFavorite(this)
                    return@setOnCheckedChangeListener
                }
                favoriteViewModel.removeFavorite(this)
                return@setOnCheckedChangeListener
            }
        }
    }

    private fun observeFavorite(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataAddFavorite.observe(this, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                    showProgressDialog(it.data ?: getString(R.string.dialog_loading))
                }
                ViewData.Status.SUCCESS -> {
                    dismissProgress()
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                }
                ViewData.Status.ERROR -> {
                    dismissProgress()
                    Toast.makeText(this, it.error?.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}