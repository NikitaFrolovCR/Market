package com.frolov.nikita.market.models.goods

import android.os.Parcel
import com.frolov.nikita.EMPTY_STRING_VALUE
import com.frolov.nikita.market.models.BaseParcelable
import com.frolov.nikita.market.models.Model
import com.frolov.nikita.market.models.goods.GoodsType.TECHNIQUE
import com.frolov.nikita.market.models.read
import com.frolov.nikita.market.models.write

interface Goods : Model<Long> {
    var name: String?
    var description: String?
    var type: GoodsType?
    var image: String?
}

class GoodsModel(override var id: Long? = null,
                 override var name: String? = EMPTY_STRING_VALUE,
                 override var description: String? = EMPTY_STRING_VALUE,
                 override var type: GoodsType? = TECHNIQUE,
                 override var image: String? = EMPTY_STRING_VALUE) : Goods {

    companion object {
        @JvmField
        val CREATOR = BaseParcelable.generateCreator {
            GoodsModel(it.read(), it.read(), it.read(), it.read(), it.read())
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = dest.write(id, name, description, type, image)

}