package com.example.gistlist.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.gistlist.R
import com.example.gistlist.ui.helper.shakeAnimation
import kotlinx.android.synthetic.main.layout_empty_state.view.*

class CustomViewEmptyState @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var message: String? = null

    init {
        inflate(context, R.layout.layout_empty_state, this)
        background = ContextCompat.getDrawable(context, R.drawable.drawable_gradient_bg)
        val padding = resources.getDimensionPixelOffset(R.dimen.default_custom_view_padding)
        setPadding(padding, padding, padding, padding)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        empty_state_image_view_icon.shakeAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        empty_state_image_view_icon.clearAnimation()
    }

    fun message(message: String?) {
        this.message = message
    }

    fun build() {
        empty_state_text_view_message.text =
            if (message.isNullOrEmpty()) context.getString(R.string.empty_state_description) else message
    }

}