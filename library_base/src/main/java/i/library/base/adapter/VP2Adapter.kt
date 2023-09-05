package i.library.base.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by hc. on 2021/11/17
 * Describe: adapter
 */
class VP2Adapter(fragment: Fragment,private val list : List<Fragment>)
    : FragmentStateAdapter(fragment) {

    fun iGetFragments(): List<Fragment>{
        return list
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}