package com.frolov.nikita.market.ui.base.pagination

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cleveroad.bootstrap.kotlin_core.utils.misc.isConnected
import com.frolov.nikita.PAGE_LIMIT
import com.frolov.nikita.market.R
import com.frolov.nikita.market.extensions.hide
import com.frolov.nikita.market.extensions.show
import com.frolov.nikita.market.models.Model
import com.frolov.nikita.market.network.exceptions.NoNetworkException
import com.frolov.nikita.market.ui.base.BaseLifecycleFragment
import com.frolov.nikita.market.ui.base.BaseViewModel
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.find

abstract class BaseListFragment<ViewModel : BaseViewModel, M : Model<Long>, A> :
        BaseLifecycleFragment<ViewModel>(),
        SwipeRefreshLayout.OnRefreshListener,
        EndlessScrollListener.OnLoadMoreListener,
        PaginationListView where A : RecyclerView.Adapter<*>, A : Adapter<M> {

    companion object {
        private const val VISIBLE_THRESHOLD = 20
    }

    protected abstract val recyclerViewId: Int

    protected abstract val noResultViewId: Int

    protected abstract val refreshLayoutId: Int

    private var endlessScrollListener: EndlessScrollListener? = null

    private lateinit var noResults: View
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var rvList: RecyclerView

    protected abstract fun getAdapter(): A?

    protected abstract fun loadInitial()

    protected abstract fun loadMoreData()

    protected open fun getLayoutManager() = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    protected open fun getScrollDirection() = EndlessScrollListener.ScrollDirection.SCROLL_DIRECTION_DOWN

    protected open fun getItemDecoration() = listOf<RecyclerView.ItemDecoration>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noResults = find(noResultViewId)
        refreshLayout = find(refreshLayoutId)
        refreshLayout.apply {
            setOnRefreshListener(this@BaseListFragment)
            setColorSchemeResources(R.color.colorPrimary)
        }
        initList()
    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = false
        if (ctx.isConnected()) loadInitial() else onError(NoNetworkException())
    }

    override fun loadMore() {
        if (ctx.isConnected()) loadMoreData()
    }

    override fun onPaginationError() {
        refreshLayout.isRefreshing = false
        endlessScrollListener?.updateNeedToLoad(true)
    }

    protected fun onInitialDataLoaded(newData: List<M>) {
        refreshLayout.isRefreshing = false
        endlessScrollListener?.reset()
        checkEndlessScroll(newData)
        getAdapter()?.dataLoad(newData)
        checkNoResults(newData.size)
    }

    protected fun onDataRangeLoaded(newData: List<M>) {
        checkEndlessScroll(newData)
        getAdapter()?.dataRangeLoad(newData)
        checkNoResults(newData.size)
        endlessScrollListener?.updateNeedToLoad(true)
    }

    protected fun enablePagination() {
        endlessScrollListener?.enable()
    }

    protected fun disablePagination() {
        endlessScrollListener?.disable()
    }

    private fun initList() {
        rvList = find(recyclerViewId)
        with(rvList) {
            adapter = this@BaseListFragment.getAdapter()
            getItemDecoration().forEach { addItemDecoration(it) }
            setHasFixedSize(false)
            layoutManager = this@BaseListFragment.getLayoutManager()
            endlessScrollListener = EndlessScrollListener.create(this,
                    VISIBLE_THRESHOLD,
                    getScrollDirection())
        }
    }

    private fun checkEndlessScroll(newData: List<M>) {
        endlessScrollListener?.onLoadMoreListener(if (newData.size < PAGE_LIMIT) null else this)
    }

    private fun checkNoResults(itemCount: Int) {
        noResults.apply { itemCount.takeIf { it == 0 }?.let { show() } ?: hide() }
    }
}