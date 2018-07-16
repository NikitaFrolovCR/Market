package com.frolov.nikita.market.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import android.view.ViewGroup

class BaseFragmentStatePagerAdapter(context: Context, fm: FragmentManager,
                                    private val fragmentInfoContainers: List<FragmentInfoContainer>)
    : FragmentStatePagerAdapter(fm) {

    companion object {
        private const val EMPTY_STRING = ""
    }

    private val appContext = context.applicationContext
    private val fragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment {
        val fragmentInfoContainer = fragmentInfoContainers[position]
        return Fragment.instantiate(appContext, fragmentInfoContainer.fragmentClass.name, fragmentInfoContainer.args)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        fragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        fragments.remove(position)
        super.destroyItem(container, position, any)
    }

    internal fun getFragment(position: Int) = fragments[position]

    override fun getPageTitle(position: Int) = fragmentInfoContainers[position].title

    override fun getCount() = fragmentInfoContainers.size

    class FragmentInfoContainer(val fragmentClass: Class<out Fragment>,
                                val title: String = EMPTY_STRING,
                                val args: Bundle = Bundle.EMPTY)
}
