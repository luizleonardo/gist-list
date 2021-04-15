package com.example.gistlist.ui.gistList

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.ext.visible
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_search_view.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class HeaderViewHolder(
    itemView: View,
    private val searchViewCallback: SearchViewCallback?
) :
    RecyclerView.ViewHolder(itemView) {

    private val compositeDisposable = CompositeDisposable()
    private var lastSearch: String = ""

    init {
        itemView.custom_view_search_view.apply {
            maxWidth = Integer.MAX_VALUE
            val appCompatImageViewClose =
                this.findViewById(R.id.search_close_btn) as? AppCompatImageView
            val searchViewEditText = this.findViewById(R.id.search_src_text) as? EditText
            searchViewEditText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        searchViewCallback?.onTextEmpty()
                        lastSearch = ""
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            if (lastSearch.isNotEmpty()) {
                setQuery(lastSearch, false)
                appCompatImageViewClose?.visible()
            }
            observeSearchView(this)
        }
    }

    private fun observeSearchView(searchView: SearchView) {
        compositeDisposable.add(RxSearchObservable.fromView(searchView)
            .debounce(RxSearchObservable.DEBOUNCE, TimeUnit.MILLISECONDS)
            .filter { query -> query.isNotEmpty() && query != lastSearch }
            .map { query -> query.toLowerCase(Locale.getDefault()).trim() }
            .switchMap { query -> Observable.just(query) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    this.lastSearch = it
                    searchViewCallback?.onSearch(it)
                },
                {
                    searchViewCallback?.onError(itemView.context.getString(R.string.generic_error))
                })
        )
    }

    interface SearchViewCallback {
        fun onSearch(query: String)
        fun onError(message: String)
        fun onTextEmpty()
    }

}