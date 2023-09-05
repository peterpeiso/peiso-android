package user.peiso.com.au.ui.dialog

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import i.library.base.adapter.IBindAdapter
import i.library.base.base.BaseDialogFragment
import i.library.base.listener.ClickInformBack
import user.peiso.com.au.BR
import user.peiso.com.au.R
import user.peiso.com.au.databinding.DialogAccountBinding
import user.peiso.com.au.databinding.ItemVenueBinding
import user.peiso.com.au.entity.InformationEntity
import user.peiso.com.au.extend.login

/**
 * Created by forever_zero on 2022/12/28.
 **/
class AccountDialog : BaseDialogFragment<DialogAccountBinding>(), ClickInformBack {

    val model = ViewModelProvider(this)[AccountVModel::class.java]

    private val adapter by lazy {
        IBindAdapter<InformationEntity.VenueUserInfo, ItemVenueBinding>(BR.dataVenue, R.layout.item_venue)
    }

    override fun getLayoutID(): Int {
        return R.layout.dialog_account
    }

    override fun initViews() {
        binding.click = this
        val list = login.information?.venueUserInfoList ?: ArrayList()
        binding.iRecycleVenues.layoutManager = LinearLayoutManager(requireContext())
        binding.iRecycleVenues.adapter = adapter
        adapter.refreshAdapterData(list)
        adapter.bindingClick = {
            login.iSetSelectVenue(it)
            dismiss()
        }
    }

    override fun onValidClick(v: View) {
        when (v.id) {
            R.id.iBtnLogOut -> {
                model.httpUserLogOut {
                    login.iOutLogin(0, true)
                }
            }
            R.id.iTextDeleteAccount -> {
                DeleteAccountDialog {
                    model.httpAccountDelete {
                        login.iOutLogin(-1, true)
                    }
                }.show(childFragmentManager, "Delete")
            }
        }
    }
}
