package id.myone.pokemongame.ui.detail

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import id.myone.pokemongame.databinding.FragmentPokemonDetailBinding
import id.myone.pokemongame.extensions.mapToPokemonState
import id.myone.pokemongame.extensions.ucFirst
import id.myone.pokemongame.models.PokemonDetail
import id.myone.pokemongame.models.PokemonStateItem
import id.myone.pokemongame.ui.barchart.BarChartFragment
import id.myone.pokemongame.ui.barchart.BarChartParam
import id.myone.pokemongame.ui.detail.tabs.AbilityTabAdapter
import id.myone.pokemongame.utils.ImageProcessing
import id.myone.pokemongame.utils.UIState
import id.myone.pokemongame.viewmodel.DetailViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PokemonDetailFragment : Fragment() {
    private lateinit var binding: FragmentPokemonDetailBinding
    private lateinit var abilityTabAdapter: AbilityTabAdapter

    private val detailViewModel by inject<DetailViewModel>()
    private val imageProcessing by inject<ImageProcessing>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pokeName = PokemonDetailFragmentArgs.fromBundle(requireArguments()).pokemonName
        getPokemonDetailByName(pokeName)
    }

    private fun setupTabLayoutForAbilityDescView(pokemon: PokemonDetail) {
        abilityTabAdapter = AbilityTabAdapter(pokemon.abilities, childFragmentManager, lifecycle)
        binding.apply {
            tabsViewsPager.adapter = abilityTabAdapter
            pokemon.abilities.forEach { tabLayout.addTab(tabLayout.newTab().setText(it.name)) }
            TabLayoutMediator(binding.tabLayout, binding.tabsViewsPager) { tab, position ->
                tab.text = pokemon.abilities[position].name
            }.attach()
        }
    }


    private fun getPokemonDetailByName(name: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.result.collect { result ->
                    result.getContentIfNotHandled()?.let { state ->
                        when (state) {
                            is UIState.Loading -> {

                            }

                            is UIState.Error -> {

                            }

                            is UIState.Success -> {
                                val (label, data) = state.data.mapToPokemonState()
                                provideSetupBarChartConfig(label, data, state.data.name)
                                providePokemonCharacterData(state.data)
                                setupTabLayoutForAbilityDescView(state.data)
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

        detailViewModel.loadPokemonByName(name)
    }

    @SuppressLint("SetTextI18n")
    private fun providePokemonCharacterData(pokemon: PokemonDetail) {
        val types = mutableListOf<String>()
        pokemon.types.forEach { types.add(it.type.name) }

        binding.apply {
            pokemonName.text = pokemon.name.ucFirst()
            pokemonHeight.text = pokemon.height.toString()
            pokemonWeight.text = pokemon.weight.toString()
            pokemonImage.setImageURI(Uri.parse(pokemon.sprites.other.dreamWorld.frontDefault))
            pokemonTypes.text = "Type:" + types.joinToString(", ")
        }

        imageProcessing.loadImage(
            requireContext(),
            pokemon.sprites.other.dreamWorld.frontDefault,
            binding.pokemonImage
        )
    }


    private fun provideSetupBarChartConfig(
        labels: ArrayList<String>,
        data: ArrayList<Int>,
        name: String,
    ) {
        if (childFragmentManager.findFragmentById(binding.pokemonStats.id) == null) {
            val fragment = BarChartFragment.newInstance().apply {
                setChartBarData(
                    BarChartParam(
                        data = arrayListOf(
                            PokemonStateItem(name, labels, data)
                        )
                    )
                )
            }

            childFragmentManager.beginTransaction().apply {
                add(binding.pokemonStats.id, fragment)
                commit()
            }
        }
    }
}