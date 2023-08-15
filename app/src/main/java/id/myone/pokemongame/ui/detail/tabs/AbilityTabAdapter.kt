/**
 * Created by Mahmud on 15/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.ui.detail.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import id.myone.pokemongame.models.AbilityData

class AbilityTabAdapter(
    private val abilities: List<AbilityData>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = abilities.size

    override fun createFragment(position: Int): Fragment {
        return AbilityTabFragment.newInstance(
            abilities[position].flavorTextEntries.map {
                it.flavorText
            }
        )
    }
}