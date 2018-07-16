package com.frolov.nikita.market.ui.base

import android.support.annotation.StringRes

interface BaseView {

    fun onError(error: Any, withResult : Boolean = false)

    fun showProgress()

    fun showProgress(isShow: Boolean)

    fun hideProgress()

    fun showSnackBar(message: String)

    fun showSnackBar(@StringRes res: Int)

    fun showSnackBar(@StringRes res: Int, @StringRes actionRes: Int, callback: () -> Unit)

    fun hideSnackBar()

}