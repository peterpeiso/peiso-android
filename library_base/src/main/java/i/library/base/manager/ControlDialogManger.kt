package i.library.base.manager

import i.library.base.base.BaseDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by forever_zero on 2022/8/9.
 **/
object ControlDialogManger {

    private var count = 0
    private val dialogArray = ArrayList<BaseDialogFragment<*>>()

    fun addDialog(dialog: BaseDialogFragment<*>){
        dialogArray.add(dialog)
        CoroutineScope(Dispatchers.IO).launch {
            delay(800)
            count = dialogArray.size
        }
    }

    fun removeDialog(dialog: BaseDialogFragment<*>){
        dialogArray.remove(dialog)
        count = dialogArray.size
    }

    fun isWaitHasDialog(): Boolean{
        return count > 0
    }

    fun isHasDialog(): Boolean{
        return dialogArray.isNotEmpty()
    }
}