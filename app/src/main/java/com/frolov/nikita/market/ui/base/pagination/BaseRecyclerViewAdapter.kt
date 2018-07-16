package com.frolov.nikita.market.ui.base.pagination


import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater

/**
 * Base adapter for recycler view
 */
abstract class BaseRecyclerViewAdapter<TData,
        TViewHolder : RecyclerView.ViewHolder>(context: Context, data: List<TData> = listOf()) :
        RecyclerView.Adapter<TViewHolder>(), Adapter<TData> {

    protected val context: Context = context.applicationContext
    protected val inflater: LayoutInflater = LayoutInflater.from(context)
    protected val data: MutableList<TData> = data.toMutableList()

    override fun getItemCount() = data.size

    @Throws(ArrayIndexOutOfBoundsException::class)
    fun getItem(position: Int): TData = data[position]

    fun isEmpty() = data.isEmpty()

    fun isNotEmpty() = data.isNotEmpty()

    fun add(`object`: TData) = data.add(`object`)

    fun add(oldPosition: Int, newPosition: Int) = data.add(newPosition, remove(oldPosition))

    operator fun set(position: Int, `object`: TData): TData = data.set(position, `object`)

    fun remove(`object`: TData) = data.remove(`object`)

    fun remove(position: Int): TData = data.removeAt(position)

    fun updateListItems(newObjects: List<TData>, callback: DiffUtil.Callback) {
        DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newObjects)
    }

    val all: List<TData>
        get() = data

    fun clear() {
        data.clear()
    }

    fun addAll(collection: Collection<TData>) = data.addAll(collection)

    val snapshot: List<TData>
        get() = data.toMutableList()

    fun getItemPosition(`object`: TData) = data.indexOf(`object`)

    fun insert(`object`: TData, position: Int) {
        data.add(position, `object`)
    }

    fun insertAll(`object`: Collection<TData>, position: Int) {
        data.addAll(position, `object`)
    }

    override fun dataLoad(newData: List<TData>) {
        clear()
        addAll(newData)
        notifyDataSetChanged()
    }

    override fun dataRangeLoad(newData: List<TData>) {
        addAll(newData)
        if (newData.isNotEmpty()) notifyItemRangeInserted(itemCount, newData.size)
    }

    override fun getItems() = data
}
