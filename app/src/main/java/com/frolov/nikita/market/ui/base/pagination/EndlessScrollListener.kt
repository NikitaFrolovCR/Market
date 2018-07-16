package com.frolov.nikita.market.ui.base.pagination

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.cleveroad.bootstrap.kotlin_core.utils.misc.assertInstanceOf

class EndlessScrollListener(private val rvToScroll: RecyclerView,
                            private val visibleThreshold: Int = DEFAULT_THRESHOLD,
                            private val direction: ScrollDirection) : RecyclerView.OnScrollListener() {
    private var previousTotal: Int = 0
    @Volatile
    private var loading = true
    private var layoutManager = rvToScroll.layoutManager as LinearLayoutManager
    private var loadMoreListener: OnLoadMoreListener? = null
    private var needToLoadMore = true
    var enabled = true

    companion object {
        private const val DEFAULT_THRESHOLD = 10

        fun create(recyclerView: RecyclerView,
                   visibleThreshold: Int,
                   direction: ScrollDirection): EndlessScrollListener {
            recyclerView.layoutManager.assertInstanceOf<LinearLayoutManager>("Layout manager")
            return EndlessScrollListener(recyclerView, visibleThreshold, direction)
        }
    }

    init {
        rvToScroll.addOnScrollListener(this)
    }

    fun onLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?): EndlessScrollListener {
        loadMoreListener = onLoadMoreListener
        return this
    }

    fun updateNeedToLoad(loadMore: Boolean) {
        needToLoadMore = loadMore
    }

    fun reset() {
        loading = false
        needToLoadMore = true
        previousTotal = 0
    }

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
        val visibleItemCount = rvToScroll.childCount
        if (loading
                && needToLoadMore
                && totalItemCount > previousTotal) {
            loading = false
            previousTotal = totalItemCount
        }
        if (direction == ScrollDirection.SCROLL_DIRECTION_DOWN
                && !loading
                && needToLoadMore
                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            loadMoreListener?.apply {
                loadMore()
                needToLoadMore = false
            }
            loading = true
        } else if (direction == ScrollDirection.SCROLL_DIRECTION_UP
                && !loading
                && needToLoadMore
                && firstVisibleItem <= visibleThreshold) {
            loadMoreListener?.apply {
                loadMore()
                needToLoadMore = false
            }
            loading = true
        }
    }

    enum class ScrollDirection {
        SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
    }

    @FunctionalInterface
    interface OnLoadMoreListener {
        fun loadMore()
    }
}