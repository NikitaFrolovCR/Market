package com.frolov.nikita.market.ui.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.functions.Consumer

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val errorLiveData = MutableLiveData<Any>()
    val isLoadingLiveData = MediatorLiveData<Boolean>()

    fun setLoadingLiveData(vararg mutableLiveData: MutableLiveData<*>) {
        mutableLiveData.forEach { liveData ->
            isLoadingLiveData.apply {
                this.removeSource(liveData)
                this.addSource(liveData) { this.value = false }
            }
        }
    }

    val onErrorConsumer = Consumer<Throwable> {
        errorLiveData.value = it.message
    }

}