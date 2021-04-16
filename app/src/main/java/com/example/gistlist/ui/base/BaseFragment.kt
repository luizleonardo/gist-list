package com.example.gistlist.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.example.gistlist.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    var alertDialog: AlertDialog? = null
    var snackBar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResource(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    @LayoutRes
    abstract fun layoutResource(): Int

    open fun setupView(view: View) {
        setupAlertDialog(view)
    }

    fun snackBarTextView() = snackBar?.view?.findViewById<AppCompatTextView>(R.id.snackbar_text)

    private fun setupAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog, null))
        alertDialog = builder.create()
    }

    fun showProgressDialog(message: String) {
        view?.context?.let {
            alertDialog?.show()
            alertDialog?.findViewById<AppCompatTextView>(R.id.text_progress_bar)?.text = message
        }
    }

    fun dismissProgress() {
        view?.context?.let {
            alertDialog?.dismiss()
        }
    }
}