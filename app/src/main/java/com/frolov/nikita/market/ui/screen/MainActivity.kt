package com.frolov.nikita.market.ui.screen

import com.frolov.nikita.market.R
import com.frolov.nikita.market.ui.base.BaseLifecycleActivity

class MainActivity : BaseLifecycleActivity<MainViewModel>() {
    override val viewModelClass = MainViewModel::class.java
    override val containerId = R.id.container
    override val layoutId = R.layout.activity_main

    override fun observeLiveData() {
        //do nothing
    }

}
