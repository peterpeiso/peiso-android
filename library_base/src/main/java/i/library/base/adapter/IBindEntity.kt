package i.library.base.adapter

import android.view.View
import i.library.base.constant.ADAPTER_TYPE_ITEM

/**
 * Created by hc. on 2021/11/15
 * Describe:
 */
class IBindEntity<T> {

    val itemType : Int = ADAPTER_TYPE_ITEM
    val itemData : T ?= null
    var itemView : View?= null

}