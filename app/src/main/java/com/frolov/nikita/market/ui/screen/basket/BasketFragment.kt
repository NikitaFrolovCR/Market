package com.frolov.nikita.market.ui.screen.basket

import android.os.Bundle
import android.view.View
import com.android.billingclient.api.*
import com.frolov.nikita.market.R
import com.frolov.nikita.market.ui.base.BaseLifecycleFragment
import kotlinx.android.synthetic.main.fragment_basket.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast

class BasketFragment : BaseLifecycleFragment<BasketViewModel>(), PurchasesUpdatedListener {
    override val viewModelClass = BasketViewModel::class.java
    override val layoutId = R.layout.fragment_basket

    companion object {
        fun newInstance() = BasketFragment().apply {
            arguments = Bundle()
        }
    }

    override fun getScreenTitle() = NO_TITLE
    override fun hasToolbar() = false
    override fun getToolbarId() = NO_TOOLBAR

    override fun observeLiveData() {
        //do nothing
    }

    private lateinit var billingClient: BillingClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bPurchase.setOnClickListener {
            purchase()
        }
        billingClient = BillingClient.newBuilder(ctx).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    val skuList = ArrayList<String>()
                    skuList.add("product")
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build()) { responseCode: Int, skuDetailsList: List<SkuDetails>? ->
                        // Process the result.
                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                            for (skuDetails in skuDetailsList) {
                                val sku = skuDetails.sku
                                val price = skuDetails.price
                                if ("product" == sku) {
                                    toast("product $price")
                                }
                            }
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

    private fun purchase() {
        val flowParams = BillingFlowParams.newBuilder()
                .setSku("product")
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build()
        val responseCode = billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        purchases?.let {
            for (purchase in purchases) {
                when (purchase.getSku()) {
                    "product" -> {
                        billingClient.consumeAsync(purchase.purchaseToken) { responseCode, purchaseToken ->
                            //inapp:com.frolov.nikita.market:android.test.purchased
                        }
                        toast("You are Premium! Congratulations!!!")
                    }
                }
            }
        }
    }

}