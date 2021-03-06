package com.example.gistlist.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.gistlist.R
import com.example.gistlist.ui.helper.shakeAnimation
import com.example.gistlist.ui.helper.visible
import kotlinx.android.synthetic.main.layout_error.view.*

class CustomViewError @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {
    enum class Type constructor(val value: Int) {
        NETWORKING(4353),
        GENERIC(2355);
    }

    private var type = Type.GENERIC
    private var callback: Callback? = null
    private var message: String? = null

    init {
        inflate(context, R.layout.layout_error, this)
        background = ContextCompat.getDrawable(context, R.drawable.drawable_gradient_bg)
        val padding = resources.getDimensionPixelOffset(R.dimen.default_custom_view_padding)
        setPadding(padding, padding, padding, padding)
        error_button_retry.setOnClickListener(this@CustomViewError)
    }

    override fun onClick(v: View?) {
        callback?.onErrorClickRetry()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        error_image_view_icon.shakeAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        error_image_view_icon.clearAnimation()
    }

    fun click(callback: Callback) = apply {
        this.callback = callback
    }

    fun type(type: Type) = apply {
        this.type = type
    }

    fun message(message: String?) {
        this.message = message
    }

    fun build() {
        when (type) {
            Type.NETWORKING -> {
                error_image_view_icon.run {
                    setImageResource(R.drawable.vector_no_internet)
                    visible()
                }
                error_text_view_title.text =
                    context.getString(R.string.error_no_internet_title)
                error_text_view_message.text =
                    context.getString(R.string.error_no_internet_description)
            }

            else -> {
                error_image_view_icon.run {
                    setImageResource(R.drawable.vector_error)
                    visible()
                }
                error_text_view_title.text =
                    context.getString(R.string.error_title)
                error_text_view_message.text =
                    if (message.isNullOrEmpty()) context.getString(R.string.error_description) else message
            }
        }
    }

    interface Callback {
        fun onErrorClickRetry() {}
    }
}