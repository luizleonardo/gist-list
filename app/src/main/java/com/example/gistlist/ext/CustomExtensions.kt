package com.example.gistlist.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.View.ALPHA
import android.view.View.TRANSLATION_Y
import android.view.animation.Animation.*
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation

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

fun View.scaleAnimation() = ScaleAnimation(
    0.7f,
    1.0f,
    0.7f,
    1.0f,
    RELATIVE_TO_SELF,
    0.7f,
    RELATIVE_TO_SELF,
    0.7f
).also {
    it.duration = 500
    it.interpolator = BounceInterpolator()
}.start()