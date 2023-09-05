package user.peiso.com.au.ui.web

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import i.library.base.base.BaseFragment
import i.library.base.base.NullVModel
import i.library.base.entity.UiStateConfig
import i.library.base.expands.navigationTo
import i.library.base.listener.ClickInformBack
import user.peiso.com.au.R
import user.peiso.com.au.databinding.UiWebBinding

/**
 * Created by hc. on 2021/12/14
 * Describe:
 */
class UiWebFragment: BaseFragment<UiWebBinding,NullVModel>(), ClickInformBack {

    companion object{

        fun launch(controller: NavController, title: String, url: String){
            val bundle = bundleOf("title" to title, "url" to url)
            controller.navigationTo(R.id.app_web,bundle)
        }
    }

    private val title by lazy {
        arguments?.getString("title")?:""
    }

    private val url by lazy {
        arguments?.getString("url")?:""
    }

    override fun onFragmentBack(): Boolean {
        return if(mViewBinding.iWebView.canGoBack()){
            mViewBinding.iWebView.goBack()
            false
        }else{
            true
        }
    }

    override val layoutID: Int
        get() = R.layout.ui_web

    override val mViewModel: NullVModel

        get() = ViewModelProvider(this)[NullVModel::class.java]

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        mViewBinding.click = this
        mViewBinding.iTextTitle.text = title
        mViewBinding.iWebView.apply {
            webChromeClient = CustomWebChromeClient()
            settings.javaScriptEnabled =    true
            loadUrl(this@UiWebFragment.url)
        }
    }

    override fun onValidClick(v: View) {
        when(v.id){
            R.id.iImageBack -> {
                nav().navigateUp()
            }
        }
    }

    override fun differentUiStateConfig(config: UiStateConfig): UiStateConfig {
        return config.apply {
            iUseTitle = true
            iUseBack = true
            iTitle = title
        }
    }

    /** 加载进度 **/
    inner class CustomWebChromeClient : WebChromeClient(){

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if(isDetached)return
            mViewBinding.iWebProgress.progress = newProgress
            if(newProgress >= 100){
                mViewBinding.iWebProgress.visibility = View.GONE
            }else{
                mViewBinding.iWebProgress.visibility = View.VISIBLE
            }
        }
    }
}