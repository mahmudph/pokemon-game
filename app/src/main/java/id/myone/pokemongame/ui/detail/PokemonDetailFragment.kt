package id.myone.pokemongame.ui.detail

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import id.myone.pokemongame.databinding.FragmentPokemonDetailBinding
import id.myone.pokemongame.extensions.mapToPokemonState
import id.myone.pokemongame.extensions.ucFirst
import id.myone.pokemongame.models.PokemonDetail
import id.myone.pokemongame.models.PokemonStateItem
import id.myone.pokemongame.ui.BaseFragment
import id.myone.pokemongame.ui.utils.barchart.BarChartFragment
import id.myone.pokemongame.ui.utils.barchart.BarChartParam
import id.myone.pokemongame.ui.detail.tabs.AbilityTabAdapter
import id.myone.pokemongame.utils.Event
import id.myone.pokemongame.utils.UIState
import id.myone.pokemongame.viewmodel.DetailViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PokemonDetailFragment : BaseFragment<FragmentPokemonDetailBinding>() {
    private lateinit var abilityTabAdapter: AbilityTabAdapter

    private val detailViewModel by inject<DetailViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPokemonDetailBinding {
        return FragmentPokemonDetailBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {}

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

    override fun <T> observableData(flow: Flow<Event<UIState<T>>>, onSuccess: (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { result ->
                    result.getContentIfNotHandled()?.let { state ->
                        when (state) {
                            is UIState.Loading -> {
                                showLoading()
                                setErrorVisibility(View.GONE)
                            }

                            is UIState.Success -> {
                                hideLoading()
                                setErrorVisibility(View.GONE)
                                onSuccess(state.data)
                            }

                            is UIState.Error -> {
                                hideLoading()
                                showSnackBar(state.message)
                                setErrorVisibility(View.VISIBLE)
                            }

                            else -> throw UnknownError("Unknown error")
                        }
                    }
                }
            }
        }
    }

    private fun setErrorVisibility(visibility: Int) {
        binding.pokemonListError.root.visibility = visibility
    }

    override fun observableViewModel() {
        observableData(detailViewModel.result) { result ->
            val (label, data) = result.mapToPokemonState()
            provideSetupBarChartConfig(label, data, result.name)
            providePokemonCharacterData(result)
            setupTabLayoutForAbilityDescView(result)
        }

        val pokeName = PokemonDetailFragmentArgs.fromBundle(requireArguments()).pokemonName
        detailViewModel.loadPokemonByName(pokeName)
    }

    @SuppressLint("SetTextI18n")
    private fun providePokemonCharacterData(pokemon: PokemonDetail) {
        val types = mutableListOf<String>()
        pokemon.types.forEach { types.add(it.type.name) }

        binding.apply {
            pokemonName.text = pokemon.name.ucFirst()
            pokemonHeight.text = pokemon.height.toString()
            pokemonWeight.text = pokemon.weight.toString()
            pokemonTypes.text = "Type:" + types.joinToString(", ")
        }

        pokemon.sprites.other.dreamWorld.frontDefault?.let { image ->
            imageProcessing.loadImage(
                requireContext(),
                image,
                binding.pokemonFront
            )
        }

        pokemon.sprites.versions.generationI.redBlue.frontDefault?.let { image ->
            imageProcessing.loadImage(
                requireContext(),
                image,
                binding.pokemonDreamWordVersion
            )
        }
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