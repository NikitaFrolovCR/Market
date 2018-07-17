package com.frolov.nikita.market.ui.screen

import android.os.Bundle
import com.frolov.nikita.market.R
import com.frolov.nikita.market.models.goods.GoodsType
import com.frolov.nikita.market.ui.base.BaseLifecycleActivity
import com.frolov.nikita.market.ui.screen.goods.GoodsFragment
import com.frolov.nikita.market.ui.screen.navigation.NavigationCallback
import com.frolov.nikita.market.ui.screen.navigation.NavigationFragment

class MainActivity : BaseLifecycleActivity<MainViewModel>(), NavigationCallback {
    override val viewModelClass = MainViewModel::class.java
    override val containerId = R.id.container
    override val layoutId = R.layout.activity_main

    override fun observeLiveData() {
        //do nothing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) replaceFragment(NavigationFragment.newInstance(), false)
    }

    override fun chooseCategory(type: GoodsType) {
        replaceFragment(GoodsFragment.newInstance(type), true)
    }
}
