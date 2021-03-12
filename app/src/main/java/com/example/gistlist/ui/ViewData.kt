package com.example.gistlist.ui

class ViewData<D>(val status: Status, val data: D? = null, val error: Throwable? = null) {
    enum class Status {
        LOADING, SUCCESS, COMPLETE, ERROR
    }
}

sealed class PlaceholderState {
    object Idle : PlaceholderState()
    object Loading : PlaceholderState()
    data class Failure(val throwable: Throwable) : PlaceholderState()
}