package com.example.gistlist.ui.gistList

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ext.gone
import com.example.gistlist.ext.startShowAnimation
import com.example.gistlist.ext.visible
import com.example.gistlist.ui.ViewData
import com.example.gistlist.ui.base.BaseFragment
import com.example.gistlist.ui.favorites.FavoriteViewModel
import com.example.gistlist.ui.gistList.RxSearchObservable.DEBOUNCE
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_repositories.*
import kotlinx.android.synthetic.main.fragment_repositories.view.*
import kotlinx.android.synthetic.main.layout_search_view.*
import kotlinx.android.synthetic.main.layout_search_view.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

class RepositoriesFragment : BaseFragment(), GistListViewHolder.FavoriteCallback {

    companion object {
        @JvmStatic
        fun newInstance(): RepositoriesFragment = RepositoriesFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_repositories

    private val gistListViewModel: GistListViewModel by viewModel()
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private val gistListAdapter = GistListAdapter(this@RepositoriesFragment)

    private val compositeDisposable = CompositeDisposable()
    private var lastSearch: String? = null
    private var appCompatImageViewClose: AppCompatImageView? = null
    private var searchViewEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        with(gistListViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observePublicGistList(this)
            observeSearch(this)
        }

        with(favoriteViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeFavorite(this)
        }

        return view
    }

    override fun setupView(view: View) {
        super.setupView(view)
        setupSnackBar(view)
        setupRecyclerView(view)
        setupSearchView(view)
        gistListViewModel.fetchPublicGists()
    }

    private fun setupSearchView(view: View) {
        view.custom_view_search_view.apply {
            maxWidth = Integer.MAX_VALUE
            appCompatImageViewClose =
                this.findViewById(R.id.search_close_btn) as? AppCompatImageView
            searchViewEditText = this.findViewById(R.id.search_src_text) as? EditText
            searchViewEditText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) gistListViewModel.fetchPublicGists()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            if (!lastSearch.isNullOrEmpty()) {
                setQuery(lastSearch, false)
                appCompatImageViewClose?.visible()
            }
            observeSearchView(this)
        }
    }

    private fun observeSearchView(searchView: SearchView) {
        compositeDisposable.add(RxSearchObservable.fromView(searchView)
            .debounce(DEBOUNCE, TimeUnit.MILLISECONDS)
            .filter { query -> query.isNotEmpty() && query != lastSearch }
            .map { query -> query.toLowerCase(Locale.getDefault()).trim() }
            .switchMap { query -> Observable.just(query) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    this.lastSearch = it
                    gistListViewModel.search(username = it)
                },
                {
                    // TODO show error
                })
        )
    }

    private fun setupRecyclerView(view: View) {
        view.fragment_gist_list_recycler_view.apply {
            val concatAdapter = ConcatAdapter(
                gistListAdapter,
            )
            layoutManager = LinearLayoutManager(
                view.context,
                RecyclerView.VERTICAL, false
            )
            adapter = concatAdapter
        }
    }

    private fun setupSnackBar(view: View) {
        snackBar = Snackbar.make(
            view.fragment_repositories_content_holder,
            "",
            Snackbar.LENGTH_SHORT
        )
    }

    private fun observePublicGistList(gistListViewModel: GistListViewModel) {
        gistListViewModel.liveDataGists.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        fragment_gist_list_progress_bar.visible()
                        fragment_gist_list_recycler_view.gone()
                    }
                    ViewData.Status.COMPLETE -> {
                        fragment_gist_list_progress_bar.gone()
                        fragment_gist_list_recycler_view.visible()
                        fragment_gist_list_recycler_view.startShowAnimation()
                        gistListAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        fragment_gist_list_progress_bar.gone()
                        fragment_gist_list_recycler_view.gone()
                    }
                }
            }
        )
    }

    private fun observeFavorite(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataAddFavorite.observe(viewLifecycleOwner, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                    showProgressDialog(it.data ?: "Loading")
                }
                ViewData.Status.SUCCESS -> {
                    dismissProgress()
                    snackBar?.run {
                        if (!isShown) this.show()
                        this.view.findViewById<AppCompatTextView>(R.id.snackbar_text).text = it.data
                    }
                }
                ViewData.Status.ERROR -> {
                    dismissProgress()
                    snackBar?.run {
                        this.view.findViewById<AppCompatTextView>(R.id.snackbar_text).text =
                            it.error?.message
                        if (!isShown) this.show() else this.dismiss()
                    }
                }
            }
        })
    }

    private fun observeSearch(gifListViewModel: GistListViewModel) {
        gifListViewModel.liveDataSearch.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        custom_view_search_view_progress.visible()
                        //fragment_gist_list_recycler_view.gone()
                        appCompatImageViewClose?.gone()
                    }
                    ViewData.Status.COMPLETE -> {
                        custom_view_search_view_progress.gone()
                        appCompatImageViewClose?.visible()
                        fragment_gist_list_recycler_view.visible()
                        fragment_gist_list_recycler_view.startShowAnimation()
                        gistListAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        appCompatImageViewClose?.visible()
                        custom_view_search_view_progress.gone()
                        fragment_gist_list_recycler_view.gone()
                        fragment_gist_list_progress_bar.gone()
                    }
                }
            }
        )
    }

    override fun onFavoriteAdd(data: GistItem) {
        favoriteViewModel.addFavorite(data)
    }

    override fun onFavoriteRemove(data: GistItem) {
        favoriteViewModel.removeFavorite(data)
    }
}