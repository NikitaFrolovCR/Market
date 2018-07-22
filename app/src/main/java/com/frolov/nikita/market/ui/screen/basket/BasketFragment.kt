package com.frolov.nikita.market.ui.screen.basket

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.android.billingclient.api.*
import com.frolov.nikita.market.R
import com.frolov.nikita.market.models.goods.Goods
import com.frolov.nikita.market.models.goods.GoodsType
import com.frolov.nikita.market.models.goods.GoodsType.*
import com.frolov.nikita.market.ui.base.pagination.BaseListFragment
import com.frolov.nikita.market.ui.screen.goods.GoodsAdapter
import com.frolov.nikita.market.ui.screen.goods.GoodsAdapterCallback
import kotlinx.android.synthetic.main.fragment_basket.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast

class BasketFragment : BaseListFragment<BasketViewModel, Goods, GoodsAdapter>(), GoodsAdapterCallback, PurchasesUpdatedListener {
    override val viewModelClass = BasketViewModel::class.java
    override val layoutId = R.layout.fragment_basket
    override val recyclerViewId = R.id.rvGoods
    override val noResultViewId = R.id.tvNoResults
    override val refreshLayoutId = R.id.swRefreshGoods

    companion object {
        fun newInstance() = BasketFragment().apply {
            arguments = Bundle()
        }
    }

    override fun getScreenTitle() = R.string.basket
    override fun hasToolbar() = true
    override fun getToolbarId() = R.id.toolbar

    override fun getAdapter() = goodsAdapter
            ?: GoodsAdapter(ctx, this).apply { goodsAdapter = this }

    override fun loadInitial() = viewModel.loadGoods()
    override fun loadMoreData() = viewModel.loadMoreGoods(getAdapter().all.last().id ?: 0)

    override fun observeLiveData() {
        with(viewModel) {
            loadGoodsLiveData.observe(this@BasketFragment, loadGoodsLiveDataObservable)
            loadMoreGoodsLiveData.observe(this@BasketFragment, loadMoreGoodsLiveDataObservable)
            refreshLiveData.observe(this@BasketFragment, refreshObserver)
            removeGoodsLiveData.observe(this@BasketFragment, removeGoodsLiveDataObservable)
            setLoadingLiveData(loadGoodsLiveData, loadMoreGoodsLiveData, refreshLiveData, removeGoodsLiveData)
        }
    }

    private val loadGoodsLiveDataObservable = Observer<List<Goods>> {
        it?.let { onInitialDataLoaded(it) }
    }
    private val loadMoreGoodsLiveDataObservable = Observer<List<Goods>> {
        it?.let { onDataRangeLoaded(it) }
    }
    private val refreshObserver = Observer<Boolean> { it?.let { swRefreshGoods.isRefreshing = it } }
    private val removeGoodsLiveDataObservable = Observer<Goods> {
        it?.let {
            goodsAdapter?.notifyDataSetChanged()
            toast("You bought ${it.name}! Congratulations!!!")
        }
    }
    private var goodsAdapter: GoodsAdapter? = null
    private lateinit var billingClient: BillingClient
    private var chooseGoods: Goods? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billingClient = BillingClient.newBuilder(ctx).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    val params = SkuDetailsParams.newBuilder()
                    val skuList = listOf(TECHNIQUE(), APPLIANCE(), SPORT(), CLOTHES())
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build()) { responseCode: Int, skuDetailsList: List<SkuDetails>? ->
                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                            viewModel.updatePrice(skuDetailsList.map { GoodsType.fromValue(it.sku) to (it.price) })
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onClickItem(goods: Goods) {
        chooseGoods = goods
        val flowParams = BillingFlowParams.newBuilder()
                .setSku(goods.type?.invoke())
                .setType(BillingClient.SkuType.INAPP)
                .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        purchases?.first()?.let {
            billingClient.consumeAsync(it.purchaseToken) { _, _ ->
                chooseGoods?.let { goods -> viewModel.removeGoods(goods) }
            }
        }
    }

}