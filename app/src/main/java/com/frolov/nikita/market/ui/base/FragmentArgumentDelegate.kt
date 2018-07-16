package com.frolov.nikita.market.ui.base

import android.os.Binder
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.BundleCompat
import android.support.v4.app.Fragment
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils.getExtra
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FragmentArgumentDelegate<T : Any> : ReadWriteProperty<Fragment, T> {

    private var value: T? = null

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (value == null) {
            val args = thisRef.arguments
                    ?: throw IllegalStateException("Cannot read property ${property.name} if no arguments have been set")
            @Suppress("UNCHECKED_CAST")
            value = args.get(getExtra(property.name, thisRef::class.java)) as T
        }
        return value ?: throw IllegalStateException("Property ${property.name} could not be read")
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val args = thisRef.arguments ?: Bundle().apply { thisRef.arguments = this }
        val key = getExtra(property.name, thisRef::class.java)

        when (value) {
            is String -> args.putString(key, value)
            is Int -> args.putInt(key, value)
            is Short -> args.putShort(key, value)
            is Long -> args.putLong(key, value)
            is Byte -> args.putByte(key, value)
            is ByteArray -> args.putByteArray(key, value)
            is Char -> args.putChar(key, value)
            is CharArray -> args.putCharArray(key, value)
            is CharSequence -> args.putCharSequence(key, value)
            is Float -> args.putFloat(key, value)
            is Bundle -> args.putBundle(key, value)
            is Binder -> BundleCompat.putBinder(args, key, value)
            is Parcelable -> args.putParcelable(key, value)
            is Serializable -> args.putSerializable(key, value)
            else -> throw IllegalStateException("Type ${value.javaClass.canonicalName} of property ${property.name} is not supported")
        }

    }

}

fun <T> Fragment.isContainsArgs(property: KProperty<T>) =
        arguments?.containsKey(getExtra(property.name, this::class.java))
                ?: throw IllegalStateException("Property ${property.name} could not be read")
