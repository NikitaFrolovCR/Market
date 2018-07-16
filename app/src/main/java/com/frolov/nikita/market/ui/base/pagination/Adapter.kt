package com.frolov.nikita.market.ui.base.pagination

interface Adapter<TData> {

    fun dataLoad(newData: List<TData>)

    fun dataRangeLoad(newData: List<TData>)

    fun getItems(): List<TData>?

}