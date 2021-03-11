package com.example.gistlist.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutResource()?.run { setContentView(this) }
        setupView()
    }

    abstract fun layoutResource(): Int?

    abstract fun setupView()
}