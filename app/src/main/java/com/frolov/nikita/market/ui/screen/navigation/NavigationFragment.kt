package com.frolov.nikita.market.ui.screen.navigation

import android.content.Context
import android.os.Bundle
import android.view.View
import com.frolov.nikita.circlemenulayout.CircleMenuInterface
import com.frolov.nikita.market.R
import com.frolov.nikita.market.models.goods.GoodsType
import com.frolov.nikita.market.models.goods.GoodsType.*
import com.frolov.nikita.market.ui.base.BaseLifecycleFragment
import kotlinx.android.synthetic.main.fragment_navigation.*

interface NavigationCallback {
    fun chooseCategory(type: GoodsType)
}

class NavigationFragment : BaseLifecycleFragment<NavigationViewModel>(), CircleMenuInterface {
    override val viewModelClass = NavigationViewModel::class.java
    override val layoutId = R.layout.fragment_navigation

    companion object {
        private const val CHOOSE_TYPE_GOODS_KEY = "CHOOSE_TYPE_GOODS"
        fun newInstance() = NavigationFragment().apply {
            arguments = Bundle()
        }
    }

    override fun getScreenTitle() = NO_TITLE
    override fun hasToolbar() = false
    override fun getToolbarId() = NO_TOOLBAR

    override fun observeLiveData() {
        //Do nothing
    }

    private var viewChoose = 0
    private var navigationCallback: NavigationCallback? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        navigationCallback = bindInterfaceOrThrow<NavigationCallback>(parentFragment, context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cmGoodsMenu.addListener(this)
        if (savedInstanceState != null)
            viewChoose = savedInstanceState.getInt(CHOOSE_TYPE_GOODS_KEY)
    }

    override fun onResume() {
        super.onResume()
        cmGoodsMenu.viewChoose = viewChoose
    }

    override fun onDetach() {
        navigationCallback = null
        super.onDetach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CHOOSE_TYPE_GOODS_KEY, cmGoodsMenu.viewChoose)
    }

    override fun onClickItem(id: Int) {
        viewChoose = cmGoodsMenu.viewChoose
        when (id) {
            R.id.llTechnique -> navigationCallback?.chooseCategory(TECHNIQUE)
            R.id.llAppliances -> navigationCallback?.chooseCategory(APPLIANCE)
            R.id.llSport -> navigationCallback?.chooseCategory(SPORT)
            R.id.llClothes -> navigationCallback?.chooseCategory(CLOTHES)
            R.id.llBusiness -> navigationCallback?.chooseCategory(BUSINESS)
        }
    }
}