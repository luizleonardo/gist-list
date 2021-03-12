package com.example.gistlist.ui.base

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.example.gistlist.R

abstract class BaseActivity : AppCompatActivity() {

    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutResource()?.run { setContentView(this) }
        setupView()
        setupToolbar()
        setupAlertDialog()
    }

    abstract fun layoutResource(): Int?

    abstract fun setupView()

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.layout_toolbar))
    }

    private fun setupAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog, null))
        alertDialog = builder.create()
    }

    fun showProgressDialog(message: String) {
        alertDialog?.show()
        alertDialog?.findViewById<AppCompatTextView>(R.id.text_progress_bar)?.text = message
    }

    fun dismissProgress() {
        alertDialog?.dismiss()
    }
}