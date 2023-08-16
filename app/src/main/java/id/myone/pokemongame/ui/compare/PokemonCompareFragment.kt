package id.myone.pokemongame.ui.compare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import id.myone.pokemongame.databinding.FragmentPokemonCompareBinding
import id.myone.pokemongame.extensions.ucFirst
import id.myone.pokemongame.models.PokemonDetail
import id.myone.pokemongame.ui.barchart.BarChartFragment
import id.myone.pokemongame.ui.barchart.BarChartParam
import id.myone.pokemongame.utils.Event
import id.myone.pokemongame.utils.ImageProcessing
import id.myone.pokemongame.utils.UIState
import id.myone.pokemongame.viewmodel.CompareViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PokemonCompareFragment : Fragment() {

    private lateinit var binding: FragmentPokemonCompareBinding

    private val viewModel by inject<CompareViewModel>()
    private val imageProcessing by inject<ImageProcessing>()

    private val clickListener = View.OnClickListener { p0 ->
        when (p0?.id) {
            binding.pokemonSection1.id -> openPokemonList(
                SELECTED_POKEMON_ONE_KEY,
                viewModel.selectedPokemonOneName.value,
            )

            binding.pokemonSection2.id -> openPokemonList(
                SELECTED_POKEMON_TWO_KEY,
                viewModel.selectedPokemonTwoName.value,
            )
        }
    }

    private val selectedPokemonListener = object : BottomSheetFragment.OnSelectedPokemonListener {
        override fun onSelectedPokemon(name: String, key: String) {
            when (key) {
                SELECTED_POKEMON_ONE_KEY -> {
                    if (viewModel.selectedPokemonTwoName.value != name) {
                        viewModel.getPokemonDetailOne(name)
                    } else {
                        Snackbar.make(binding.root, "Pokemon already selected", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
                SELECTED_POKEMON_TWO_KEY ->     {
                    if (viewModel.selectedPokemonOneName.value != name) {
                        viewModel.getPokemonDetailTwo(name)
                    } else {
                        Snackbar.make(binding.root, "Pokemon already selected", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            Log.i(this.javaClass.name, "Selected Pokemon: $name")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPokemonCompareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observablePokemonDetails()
        setupView()
    }

    private fun setupView() {
        binding.pokemonSection1.setOnClickListener(clickListener)
        binding.pokemonSection2.setOnClickListener(clickListener)
    }

    private fun observablePokemonDetail(
        pokemonDetailFlow: Flow<Event<UIState<PokemonDetail>>>,
        onSuccess: (PokemonDetail) -> Unit,
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pokemonDetailFlow.collect { result ->

                    result.getContentIfNotHandled()?.let { state ->
                        when (state) {
                            is UIState.Loading -> {
                                binding.pokemonCompareLoading.root.visibility = View.VISIBLE
                            }

                            is UIState.Success -> {
                                binding.pokemonCompareLoading.root.visibility = View.GONE
                                onSuccess(state.data)
                            }

                            is UIState.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT)
                                    .show()
                            }

                            else -> {}
                        }
                    }
                    Log.i(this.javaClass.name, "Pokemon Detail : $result")
                }
            }
        }
    }

    private fun observablePokemonDetails() {
        observablePokemonDetail(viewModel.pokemonDetailOne) { result ->
            Log.i(this.javaClass.name, "Pokemon Detail One : $result")
            binding.apply {
                section1Name.text = result.name.ucFirst()
                imageProcessing.loadImage(
                    requireContext(),
                    result.sprites.versions.generationI.redBlue.frontDefault,
                    pokemon1Image
                )
            }
        }

        observablePokemonDetail(viewModel.pokemonDetailTwo) { result ->
            Log.i(this.javaClass.name, "Pokemon Detail Two : $result")
            binding.apply {
                section2Name.text = result.name.ucFirst()
                imageProcessing.loadImage(
                    requireContext(),
                    result.sprites.versions.generationI.redBlue.frontDefault,
                    pokemon2Image
                )
            }
        }

        /**
         * set the pokemon statistic when both pokemon detail is loaded
         */

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.comparePokemonResult.collect { compareResult ->

                    if (compareResult != null) {
                        val fragment = BarChartFragment.newInstance().apply {
                            setChartBarData(
                                BarChartParam(
                                    data = arrayListOf(
                                        compareResult.source,
                                        compareResult.target,
                                    )
                                )
                            )
                        }

                        childFragmentManager.beginTransaction().apply {
                            replace(binding.pokemonCompareChart.id, fragment)
                            commit()
                        }
                    }
                }
            }
        }
    }

    private fun openPokemonList(key: String, selectedPokName: String? = null) {

        val bottomSheet = BottomSheetFragment.newInstance(key, selectedPokName).apply {
            setOnSelectedPokemonListener(selectedPokemonListener)
        }
        bottomSheet.show(parentFragmentManager, "bottom-sheet")
    }

    companion object {
        private const val SELECTED_POKEMON_ONE_KEY = "selected_pokemon_one_key"
        private const val SELECTED_POKEMON_TWO_KEY = "selected_pokemon_two_key"
    }
}