/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.ui.list.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.myone.pokemongame.BuildConfig
import id.myone.pokemongame.R
import id.myone.pokemongame.databinding.PokemonItemBinding
import id.myone.pokemongame.extensions.getIdFromUrl
import id.myone.pokemongame.extensions.ucFirst
import id.myone.pokemongame.models.PokemonData
import id.myone.pokemongame.utils.ImageProcessing

class PokemonListAdapter(
    private val context: Context,
    private val imageProcessing: ImageProcessing,
    private val selectedPokemonName: String? = null,
) :
    PagingDataAdapter<PokemonData, PokemonListAdapter.PokemonViewHolder>(DIFF_CALLBACK) {

    private lateinit var onClickListener: OnClickListener

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = PokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(
            binding,
            onClickListener,
            imageProcessing,
            selectedPokemonName,
            context
        )
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val data = getItem(position)
        data?.let { holder.bind(data) }
    }

    class PokemonViewHolder(
        private val binding: PokemonItemBinding,
        private val onClickListener: OnClickListener,
        private val imageProcessing: ImageProcessing,
        private val selectedPokemonName: String? = null,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: PokemonData) {

            val pokemonNo = data.url.getIdFromUrl()

            imageProcessing.loadImage(
                context,
                BuildConfig.HOST_IMAGE_URL + "$pokemonNo.svg",
                binding.image
            )

            binding.apply {

                setSelectedItem(R.color.white)
                setSelectedTextColor(R.color.black)
                /**
                 * when selected pokemon name is same with current pokemon name
                 * then set the background as selected
                 */
                if (selectedPokemonName == data.name) {
                    root.isSelected = true
                    setSelectedItem(R.color.green_m300)
                    setSelectedTextColor(R.color.white)

                } else root.isSelected = false

                pokemonId.text = "#00$pokemonNo"
                title.text = data.name.ucFirst()

                root.setOnClickListener { onClickListener.click(data.name) }
            }
        }

        private fun setSelectedItem(color: Int) {
            binding.root.backgroundTintList = ContextCompat.getColorStateList(
                context, color
            )
        }

        private fun setSelectedTextColor(color: Int) {
            binding.title.setTextColor(ContextCompat.getColor(context, color))
            binding.pokemonId.setTextColor(ContextCompat.getColor(context, color))
        }
    }

    interface OnClickListener {
        fun click(q: String)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PokemonData>() {
            override fun areItemsTheSame(
                oldItem: PokemonData,
                newItem: PokemonData,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PokemonData,
                newItem: PokemonData,
            ): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}