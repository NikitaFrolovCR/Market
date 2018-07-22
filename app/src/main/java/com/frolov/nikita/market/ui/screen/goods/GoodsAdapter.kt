package com.frolov.nikita.market.ui.screen.goods

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.frolov.nikita.market.R
import com.frolov.nikita.market.extensions.loadImage
import com.frolov.nikita.market.extensions.setClickListeners
import com.frolov.nikita.market.models.goods.Goods
import com.frolov.nikita.market.ui.base.pagination.Adapter
import com.frolov.nikita.market.ui.base.pagination.BaseRecyclerViewAdapter
import org.jetbrains.anko.find
import java.lang.ref.WeakReference

interface GoodsAdapterCallback {
    fun onClickItem(goods: Goods)
}

interface AdapterCallback {
    fun onClickItem(position: Int)
}

class GoodsAdapter(context: Context, callback: GoodsAdapterCallback) :
        BaseRecyclerViewAdapter<Goods, GoodsAdapter.GoodsViewHolder>(context), Adapter<Goods>, AdapterCallback {

    private val weakRefCallback = WeakReference(callback)

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) = getItem(position).let { holder.bind(it) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoodsViewHolder.newInstance(inflater, parent, this)

    override fun onClickItem(position: Int) {
        getItem(position).let { weakRefCallback.get()?.onClickItem(it) }
    }

    class GoodsViewHolder(itemView: View, private val callback: AdapterCallback?) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

        private val ivImageGoods = itemView.find<ImageView>(R.id.ivImageGoods)
        private val tvGoodsName = itemView.find<TextView>(R.id.tvGoodsName)
        private val tvGoodsDescription = itemView.find<TextView>(R.id.tvGoodsDescription)
        private val tvGoodsPrice = itemView.find<TextView>(R.id.tvGoodsPrice)
        private val rlRootContainer = itemView.find<View>(R.id.rlRootContainer)

        companion object {
            fun newInstance(inflater: LayoutInflater, parent: ViewGroup?, callback: AdapterCallback?) =
                    GoodsViewHolder(inflater.inflate(R.layout.goods_item, parent, false), callback)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.rlRootContainer -> callback?.onClickItem(adapterPosition)
            }
        }

        fun bind(goods: Goods) {
            setClickListeners(rlRootContainer)
            goods.image?.let { ivImageGoods.loadImage(it) }
                    ?: ivImageGoods.loadImage(R.drawable.ic_clear_black_24dp)
            tvGoodsName.text = goods.name
            tvGoodsDescription.text = goods.description
            tvGoodsPrice.text = goods.price
        }
    }
}