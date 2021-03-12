package com.example.gistlist.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.View.ALPHA
import android.view.View.TRANSLATION_Y

fun View.gone(): View {
    this.visibility = View.GONE
    return this
}

fun View.visible(): View {
    this.visibility = View.VISIBLE
    return this
}

fun View.startShowAnimation() {
    alpha = 0f

    val animatorSet = AnimatorSet().apply {
        duration = 600
        startDelay = 0

        playTogether(
            ObjectAnimator.ofPropertyValuesHolder(
                this@startShowAnimation,
                PropertyValuesHolder.ofFloat(TRANSLATION_Y, 100f, 0f),
                PropertyValuesHolder.ofFloat(ALPHA, 0f, 1f)
            )
        )
    }

    animatorSet.start()
}