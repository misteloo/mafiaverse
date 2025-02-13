package ir.greendex.mafia.ui.group.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class GroupTabAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val fragments by lazy { mutableListOf<Fragment>() }

    companion object {
        private const val TAB_COUNT = 3
    }

    override fun getItemCount() = TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> fragments[position]
            1-> fragments[position]
            else -> fragments[2]
        }
    }

    fun addFragments(fragments: List<Fragment>) {
        this.fragments.addAll(fragments)
    }
}