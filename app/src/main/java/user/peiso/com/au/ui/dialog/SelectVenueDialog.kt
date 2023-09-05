package user.peiso.com.au.ui.dialog

import androidx.recyclerview.widget.LinearLayoutManager
import i.library.base.adapter.IBindAdapter
import i.library.base.base.BaseDialogFragment
import user.peiso.com.au.BR
import user.peiso.com.au.R
import user.peiso.com.au.databinding.DialogVenueBinding
import user.peiso.com.au.databinding.ItemVenueBinding
import user.peiso.com.au.entity.InformationEntity

/**
 * Created by forever_zero on 2022/12/28.
 **/
class SelectVenueDialog(
    private val list: List<InformationEntity.VenueUserInfo>,
    private val callback: (InformationEntity.VenueUserInfo) -> Unit)
    : BaseDialogFragment<DialogVenueBinding>() {

    private val adapter by lazy {
        IBindAdapter<InformationEntity.VenueUserInfo,ItemVenueBinding>(BR.dataVenue,R.layout.item_venue)
    }

    override fun getLayoutID(): Int {
        return R.layout.dialog_venue
    }

    override fun initViews() {
        binding.iRecycleVenues.layoutManager = LinearLayoutManager(requireContext())
        binding.iRecycleVenues.adapter = adapter
        adapter.refreshAdapterData(list)
        adapter.bindingClick = {
            callback.invoke(it)
            dismiss()
        }
    }
}