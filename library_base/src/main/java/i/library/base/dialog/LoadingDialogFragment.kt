package i.library.base.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentManager
import i.library.base.R
import i.library.base.base.BaseDialogFragment
import i.library.base.databinding.DialogLoadingBinding
import i.library.base.expands.logo

/**
 * Created by hc. on 2021/11/8
 * Describe: 加载中状态
 */
class LoadingDialogFragment : BaseDialogFragment<DialogLoadingBinding>(){

    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun getLayoutID(): Int {
        return R.layout.dialog_loading
    }

    override fun initViews() { }

    override fun getDAnimation(): Int {
        return R.style.DialogAnimation
    }
}