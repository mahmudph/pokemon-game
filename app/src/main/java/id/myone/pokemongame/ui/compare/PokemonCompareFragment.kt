package id.myone.pokemongame.ui.compare

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.myone.pokemongame.R
import id.myone.pokemongame.databinding.FragmentPokemonCompareBinding
import id.myone.pokemongame.extensions.ucFirst
import id.myone.pokemongame.ui.BaseFragment
import id.myone.pokemongame.ui.utils.barchart.BarChartFragment
import id.myone.pokemongame.ui.utils.barchart.BarChartParam
import id.myone.pokemongame.viewmodel.CompareViewModel
import org.koin.android.ext.android.inject

class PokemonCompareFragment : BaseFragment<FragmentPokemonCompareBinding>() {
    private val viewModel by inject<CompareViewModel>()

    private val clickListener = View.OnClickListener { p0 ->
        when (p0?.id) {
            binding.pokemonSection1.id -> openPokemonList(
                SELECTED_POKEMON_ONE_KEY, viewModel.selectedPokemonOneName.value,
            )

            binding.pokemonSection2.id -> openPokemonList(
                SELECTED_POKEMON_TWO_KEY, viewModel.selectedPokemonTwoName.value,
            )
        }
    }

    private val selectedPokemonListener = object : BottomSheetFragment.OnSelectedPokemonListener {
        override fun onSelectedPokemon(name: String, key: String) {
            Log.i(this.javaClass.name, "Selected Pokemon: $name")
            when (key) {
                SELECTED_POKEMON_ONE_KEY -> {
                    if (viewModel.selectedPokemonTwoName.value != name) viewModel.getPokemonDetailOne(
                        name
                    )
                    else showSnackBar(getString(R.string.pokemon_already_selected))
                }

                SELECTED_POKEMON_TWO_KEY -> {
                    if (viewModel.selectedPokemonOneName.value != name) viewModel.getPokemonDetailTwo(
                        name
                    )
                    else showSnackBar(getString(R.string.pokemon_already_selected))
                }
            }
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPokemonCompareBinding {
        return FragmentPokemonCompareBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {
        binding.pokemonSection1.setOnClickListener(clickListener)
        binding.pokemonSection2.setOnClickListener(clickListener)
    }

    override fun observableViewModel() {

        observableData(viewModel.pokemonDetailOne) { result ->
            Log.i(this.javaClass.name, "Pokemon Detail One : $result")
            binding.apply {
                section1Name.text = result.name.ucFirst()
                result.sprites.versions.generationI.redBlue.frontDefault?.let {
                    imageProcessing.loadImage(
                        requireContext(),
                        it,
                        pokemon1Image
                    )
                }
            }
        }

        observableData(viewModel.pokemonDetailTwo) { result ->
            Log.i(this.javaClass.name, "Pokemon Detail Two : $result")
            binding.apply {
                section2Name.text = result.name.ucFirst()
                result.sprites.versions.generationI.redBlue.frontDefault?.let {
                    imageProcessing.loadImage(
                        requireContext(),
                        it,
                        pokemon2Image
                    )
                }
            }
        }

        observableSimpleData(viewModel.comparePokemonResult) { result ->
            if (result != null) {
                setPokemonChartVisibility(View.VISIBLE)
                val fragment = BarChartFragment.newInstance().apply {
                    setChartBarData(
                        BarChartParam(
                            data = arrayListOf(result.source, result.target)
                        )
                    )
                }

                childFragmentManager.beginTransaction().apply {
                    replace(binding.pokemonCompareChart.id, fragment)
                    commit()
                }
            } else {
                setPokemonChartVisibility(View.GONE)
            }
        }
    }

    private fun setPokemonChartVisibility(visibility: Int) {
        binding.apply {
            pokemonCompareChart.visibility = visibility
            if (visibility == View.VISIBLE) {
                pokemonCompareMessage.visibility = View.GONE
            } else pokemonCompareMessage.visibility = View.VISIBLE
        }
    }

    private fun openPokemonList(key: String, selectedPokName: String? = null) {

        val bottomSheet = BottomSheetFragment.newInstance(key, selectedPokName).apply {
            setOnSelectedPokemonListener(selectedPokemonListener)
        }

        bottomSheet.show(parentFragmentManager, BottomSheetFragment::class.java.name)
    }

    companion object {
        private const val SELECTED_POKEMON_ONE_KEY = "selected_pokemon_one_key"
        private const val SELECTED_POKEMON_TWO_KEY = "selected_pokemon_two_key"
    }
}