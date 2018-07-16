package com.frolov.nikita.market.ui.base.dialog

import android.content.Intent


interface DialogFragmentCallback {
    fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent = Intent())
}