package com.example.gistlist.ui.base

import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    //val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        //compositeDisposable.dispose()
        super.onCleared()
    }
}