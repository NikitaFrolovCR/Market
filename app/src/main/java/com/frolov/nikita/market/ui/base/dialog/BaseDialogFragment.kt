package com.frolov.nikita.market.ui.base.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils.getExtra


abstract class BaseDialogFragment : DialogFragment() {

    protected abstract val layoutId: Int

    protected abstract val gravity: Int

    protected abstract val dialogSize: DialogSize

    companion object {
        val REQUEST_CODE_EXTRA = getExtra("REQUEST_CODE_EXTRA", BaseDialogFragment::class.java)
        private const val DEFAULT_REQUEST_CODE = -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    @SuppressLint("MissingSuperCall")
    override fun onResume() {
        super.onResume()
        with(dialog.window) {
            attributes.gravity = gravity
            setLayout(dialogSize.width, dialogSize.height)
        }
    }

    fun <T> showForResult(fragment: T, requestCode: Int) where T : Fragment, T : DialogFragmentCallback {
        setTargetFragment(fragment, requestCode)
        show(fragment.fragmentManager, fragment.javaClass.simpleName)
    }

    fun <T> showForResult(activity: T, requestCode: Int) where T : AppCompatActivity, T : DialogFragmentCallback {
        arguments?.putInt(REQUEST_CODE_EXTRA, requestCode)
        show(activity.supportFragmentManager, activity.javaClass.name)
    }

    protected fun setResult(resultCode: Int, data: Intent = Intent()) {
        var callback: DialogFragmentCallback? = null
        var requestCode = DEFAULT_REQUEST_CODE

        val fragment = targetFragment
        if (fragment != null && fragment is DialogFragmentCallback) {
            callback = fragment
            requestCode = targetRequestCode
        } else if (fragment == null) {
            val activity = activity
            if (activity is DialogFragmentCallback) {
                callback = activity
                requestCode = arguments?.getInt(REQUEST_CODE_EXTRA, DEFAULT_REQUEST_CODE) ?: DEFAULT_REQUEST_CODE
            }
        }
        if (requestCode != DEFAULT_REQUEST_CODE) callback?.onDialogResult(requestCode, resultCode, data)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        setResult(Activity.RESULT_CANCELED)
        super.onDismiss(dialog)
    }
}