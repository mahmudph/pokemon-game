/**
 * Created by Mahmud on 15/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.ui.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.myone.pokemongame.databinding.AbilityItemBinding

class AbilityDescListAdapter(
    private val abilities: List<String>,
) : RecyclerView.Adapter<AbilityDescListAdapter.ViewHolder>() {

    private lateinit var binding: AbilityItemBinding

    inner class ViewHolder(
        private val binding: AbilityItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(desc: String) {
            binding.apply {
                abilityDescription.text = desc
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = AbilityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return abilities.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(abilities[position])
    }
}