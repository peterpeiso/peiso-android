package i.library.base.entity

import i.library.base.utils.AMathUtils

/**
 * Created by hc. on 2022/1/13
 * Describe: Config
 */
class UiStateConfig {

    companion object {
        var I_CURRENT_STATE_CONFIG = -1L
    }

    /* background */
    var backgroundColor:Int ?= null
    var backgroundMipmap:Int ?= null

    /* title */
    var iUseBar = true
    var iUseTitle = false
    var iUseBack = false
    var logo = false
    var iTitle = " "
    var iRightText = ""
    var iElevation = 0f

    /* back */
    var back: (()->Unit) ?= null
    var rightClick: (()->Unit) ?= null

    val id by lazy{
        AMathUtils.createFakeID("$iUseBar " +
                "$iUseTitle " +
                "$iUseBack " +
                "$iTitle " +
                "$iElevation " +
                "$iRightText " +
                "$backgroundColor " +
                "$backgroundMipmap")
    }
}