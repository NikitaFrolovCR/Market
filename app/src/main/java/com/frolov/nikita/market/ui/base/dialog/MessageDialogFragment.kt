package com.frolov.nikita.market.ui.base.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.frolov.nikita.market.R
import com.frolov.nikita.market.extensions.hide
import com.frolov.nikita.market.extensions.setClickListeners
import com.frolov.nikita.market.ui.base.FragmentArgumentDelegate
import com.frolov.nikita.market.ui.base.isContainsArgs
import kotlinx.android.synthetic.main.dialog_fragment_message.*


class MessageDialogFragment : BaseDialogFragment(), View.OnClickListener {
    override val layoutId = R.layout.dialog_fragment_message
    override val gravity = Gravity.CENTER
    override val dialogSize
        get() = DialogSize(resources.getDimensionPixelSize(R.dimen.message_dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT)

    companion object {
        fun newInstance(title: String, message: String? = null) = MessageDialogFragment().apply {
            this.title = title
            message?.let { this.message = it }
        }
    }

    private var title by FragmentArgumentDelegate<String>()
    private var message by FragmentArgumentDelegate<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setCanceledOnTouchOutside(false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners(tvOk)
        tvTitle.text = title
        if (isContainsArgs(::message)) tvSubTitle.text = message else tvSubTitle.hide(true)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.tvOk) {
            dismiss()
        }
    }

}