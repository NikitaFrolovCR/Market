package com.frolov.nikita.market.ui.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import com.cleveroad.bootstrap.kotlin_core.utils.misc.hideKeyboard
import com.cleveroad.bootstrap.kotlin_core.utils.withNotNull
import com.frolov.nikita.EMPTY_STRING_VALUE
import com.frolov.nikita.KEYBOARD_VISIBLE_THRESHOLD_DP
import com.frolov.nikita.market.R
import com.frolov.nikita.market.ui.base.dialog.DialogFragmentCallback
import org.jetbrains.anko.find


abstract class BaseLifecycleFragment<T : BaseViewModel> : Fragment(), BaseView, DialogFragmentCallback {

    companion object {
        const val NO_TOOLBAR = -1
        const val NO_TITLE = -1
        const val TITLE_STRING = -2
    }

    protected var toolbar: Toolbar? = null

    private var backPressedCallback: BackPressedCallback? = null

    abstract val viewModelClass: Class<T>

    protected val viewModel: T by lazy {
        ViewModelProviders.of(this).get(viewModelClass)
    }

    protected abstract fun observeLiveData()

    protected abstract val layoutId: Int

    private var baseView: BaseView? = null

    private val textWatchers: Map<EditText?, TextWatcher> = mutableMapOf()

    private val keyboardListener = ViewTreeObserver.OnGlobalLayoutListener {
        withNotNull(view) {
            val rect = Rect()
            getWindowVisibleDisplayFrame(rect)
            when {
                !blockKeyboardListener && rootView.height - (rect.bottom - rect.top) > KEYBOARD_VISIBLE_THRESHOLD_DP -> {
                    blockKeyboardListener = true
                    onKeyboardSwitch(true)
                }
                blockKeyboardListener && rootView.height - (rect.bottom - rect.top) <= KEYBOARD_VISIBLE_THRESHOLD_DP -> {
                    blockKeyboardListener = false
                    onKeyboardSwitch(false)
                }
            }
        }
    }
    private var blockKeyboardListener: Boolean = true

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        baseView = bindInterfaceOrThrow<BaseView>(parentFragment, context)
        backPressedCallback = bindInterfaceOrThrow<BackPressedCallback>(parentFragment, context)
    }

    override fun onDestroyView() {
        textWatchers.forEach { (key, value) -> key?.removeTextChangedListener(value) }
        if (needKeyboardListener()) view?.viewTreeObserver?.removeOnGlobalLayoutListener(keyboardListener)
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDetach() {
        baseView = null
        backPressedCallback = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(layoutId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAllData()
        if (needKeyboardListener()) view.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
        initToolbar()
    }

    private fun observeAllData() {
        observeLiveData()
        with(viewModel) {
            isLoadingLiveData.observe(this@BaseLifecycleFragment, Observer<Boolean> {
                it?.let {
                    hideKeyboard()
                    showProgress(it)
                    isLoadingLiveData.value = null
                }
            })
            errorLiveData.observe(this@BaseLifecycleFragment, Observer<Any> {
                it?.let {
                    onError(it, hasResultFromDialog())
                    errorLiveData.value = null
                }
            })
        }
    }

    fun backPressed() {
        backPressedCallback?.backPressed()
    }

    fun EditText.addTextWatcher(watcher: TextWatcher) = this.apply {
        textWatchers.plus(this to watcher)
        addTextChangedListener(watcher)
    }

    /**
     * Setup action bar
     *
     * @param actionBar Modified action bar
     */
    protected fun setupActionBar(actionBar: ActionBar) {
        actionBar.apply {
            setHomeAsUpIndicator(getHomeAsUpIndicator())
            title = getStringScreenTitle()
            setDisplayHomeAsUpEnabled(needToShowBackNav())
        }
    }

    /**
     * Set if need to show back navigation in toolbar
     *
     * @return True if toolbar has back navigation
     * False if toolbar without back navigation
     */
    protected open fun needToShowBackNav() = true

    /**
     * Set [String] screen title
     *
     * @return Screen title
     */
    protected open fun getStringScreenTitle() =
            if (getScreenTitle() != NO_TITLE) {
                getString(getScreenTitle())
            } else {
                EMPTY_STRING_VALUE
            }

    @DrawableRes
    protected open fun getHomeAsUpIndicator(): Int = R.drawable.ic_arrow_white_24dp

    /**
     * Set id of screen title
     *
     * @return Id of screen title
     */
    @StringRes
    protected abstract fun getScreenTitle(): Int

    /**
     * Set if fragment has toolbar
     *
     * @return True if fragment has toolbar
     * False if fragment without toolbar
     */
    protected abstract fun hasToolbar(): Boolean

    /**
     * Set id of toolbar
     *
     * @return Toolbar id
     */
    @IdRes
    protected abstract fun getToolbarId(): Int

    /**
     * Initialize toolbar
     */
    protected fun initToolbar() {
        view?.apply {
            if (hasToolbar() && getToolbarId() != NO_TOOLBAR) {
                toolbar = find(getToolbarId())
                with(activity as AppCompatActivity) {
                    setSupportActionBar(toolbar)
                    supportActionBar?.let {
                        setupActionBar(it)
                        if (needToShowBackNav()) {
                            toolbar?.setNavigationOnClickListener { handleNavigation() }
                        }
                    }
                }
            }
        }
    }

    protected open fun handleNavigation() {
        backPressedCallback?.backPressed()
    }

    override fun showProgress() {
        baseView?.showProgress()
    }

    override fun hideProgress() {
        baseView?.hideProgress()
    }

    override fun showSnackBar(message: String) {
        baseView?.showSnackBar(message)
    }

    override fun showSnackBar(res: Int) {
        baseView?.showSnackBar(res)
    }

    override fun hideSnackBar() {
        baseView?.hideSnackBar()
    }

    override fun showProgress(isShow: Boolean) {
        if (isShow) showProgress() else hideProgress()
    }

    override fun showSnackBar(res: Int, actionRes: Int, callback: () -> Unit) {
        baseView?.showSnackBar(res, actionRes, callback)
    }

    override fun onError(error: Any, withResult: Boolean) {
        baseView?.onError(error, withResult)
    }

    protected inline fun <reified T> bindInterfaceOrThrow(vararg objects: Any?):
            T = objects.find { it is T } as T
            ?: throw NotImplementedInterfaceException(T::class.java)

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        //Do nothing
    }

    protected open fun hasResultFromDialog(): Boolean = false

    protected open fun needKeyboardListener() = false

    protected open fun onKeyboardSwitch(isShow: Boolean) {
        //Do nothing
    }
}