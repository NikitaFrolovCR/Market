package com.frolov.nikita.market.ui.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.cleveroad.bootstrap.kotlin_core.utils.misc.hideKeyboard
import com.frolov.nikita.market.R
import com.frolov.nikita.market.extensions.bindInterface
import com.frolov.nikita.market.extensions.hide
import com.frolov.nikita.market.extensions.safeLet
import com.frolov.nikita.market.extensions.show
import com.frolov.nikita.market.ui.base.dialog.DialogFragmentCallback
import com.frolov.nikita.market.ui.base.dialog.MessageDialogFragment
import org.jetbrains.anko.find


@Suppress("LeakingThis")
abstract class BaseLifecycleActivity<T : BaseViewModel> : AppCompatActivity(),
        BaseView,
        BackPressedCallback,
        DialogFragmentCallback {

    companion object {
        private const val SNACK_BAR_DURATION = 5_000
        private const val SNACK_BAR_MAX_LINES = 5
    }

    private var snackBar: Snackbar? = null

    protected abstract val viewModelClass: Class<T>

    protected abstract val containerId: Int

    protected abstract val layoutId: Int

    protected abstract fun observeLiveData()

    private var progressView: FrameLayout? = null

    protected val viewModel: T by lazy {
        ViewModelProviders.of(this).get(viewModelClass)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        if (hasProgressBar()) progressView = find(getProgressBarId())
        observeAllData()
    }

    private fun observeAllData() {
        observeLiveData()
        with(viewModel) {
            isLoadingLiveData.observe(this@BaseLifecycleActivity, Observer<Boolean> { it?.let { showProgress(it) } })
            errorLiveData.observe(this@BaseLifecycleActivity, Observer<Any> { it?.let { onError(it) } })
        }
    }

    protected open fun hasProgressBar(): Boolean = false

    private fun getProgressBarId() = R.id.progressView

    protected fun replaceFragment(fragment: Fragment, needToAddToBackStack: Boolean = true) {
        hideKeyboard()
        val name = fragment.javaClass.simpleName
        supportFragmentManager.beginTransaction().apply {
            replace(containerId, fragment, name)
            if (needToAddToBackStack) addToBackStack(name)
        }.commit()
    }

    protected fun removeFragmentByTag(vararg tags: String) {
        with(supportFragmentManager) {
            for(fragmentTag in tags){
                findFragmentByTag(fragmentTag)?.let {
                    beginTransaction().apply {
                        remove(it)
                    }.commit()
                    popBackStack()
                }
            }
        }
    }

    override fun showProgress() {
        progressView?.show()
    }

    override fun hideProgress() {
        progressView?.hide(false)
    }

    override fun showSnackBar(message: String) {
        showSnackBar(find(android.R.id.content), message)
    }

    override fun showSnackBar(res: Int) {
        showSnackBar(find(android.R.id.content), getString(res))
    }

    override fun showSnackBar(res: Int, actionRes: Int, callback: () -> Unit) {
        showSnackBarWithAction(find(android.R.id.content), res, actionRes, callback)
    }

    private fun showSnackBar(rootView: View?, text: String?) {
        safeLet(rootView, text) { view, txt ->
            snackBar = Snackbar.make(view, txt, SNACK_BAR_DURATION)
                    .apply {
                        setUpSnackBarView(this.view, this)
                        show()
                    }
        }
    }

    override fun onError(error: Any, withResult: Boolean) {
        hideProgress()
        var title = getString(R.string.dialog_title_default)
        var message = getString(R.string.dialog_subtitle_default)
        when (error) {
            is String -> {
                message = error
            }
            is Exception -> {
                message = error.message
            }
        }
        val dialogFragment = MessageDialogFragment.newInstance(title, message)
        if (withResult) {
            dialogFragment.showForResult(this, RequestCodes.RequestCode.REQUEST_DIALOG_MESSAGE())
        } else {
            dialogFragment.show(supportFragmentManager, MessageDialogFragment::class.java.simpleName)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.isNotEmpty()) {
            bindInterface<OnBackPressedListener>(supportFragmentManager.fragments.last()) {
                if (onBackPressed()) return
            }
        }
        super.onBackPressed()
    }

    private fun showSnackBarWithAction(rootView: View?, res: Int, actionRes: Int, callback: () -> Unit) {
        rootView?.let {
            snackBar = Snackbar.make(it, res, SNACK_BAR_DURATION).apply {
                setAction(actionRes, { _ -> callback() })
                setUpSnackBarView(this.view, this)
                show()
            }
        }
    }

    private fun setUpSnackBarView(snackBarView: View, snackBar: Snackbar) = with(snackBarView) {
        setOnClickListener { snackBar.dismiss() }
        setBackgroundResource(R.color.colorPrimary)
        with(find<View>(android.support.design.R.id.snackbar_text) as TextView) {
            maxLines = SNACK_BAR_MAX_LINES
        }
    }

    override fun hideSnackBar() {
        snackBar?.let { if (it.isShown) it.dismiss() }
    }

    override fun showProgress(isShow: Boolean) {
        if (isShow) showProgress() else hideProgress()
    }

    override fun backPressed() {
        with(supportFragmentManager) {
            backStackEntryCount.takeUnless { it == 0 }?.let { popBackStack() } ?: onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.findFragmentById(containerId)?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        supportFragmentManager.findFragmentById(containerId)?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        val fragment = supportFragmentManager.findFragmentById(containerId)
        if (fragment is DialogFragmentCallback) fragment.onDialogResult(requestCode, resultCode, data)
    }
}