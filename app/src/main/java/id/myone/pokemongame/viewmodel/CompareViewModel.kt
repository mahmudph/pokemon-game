/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.myone.pokemongame.extensions.mapToPokemonState
import id.myone.pokemongame.models.ComparePokemonData
import id.myone.pokemongame.models.PokemonDetail
import id.myone.pokemongame.models.PokemonStateItem
import id.myone.pokemongame.repository.AppRepositoryContract
import id.myone.pokemongame.repository.ResultData
import id.myone.pokemongame.utils.Event
import id.myone.pokemongame.utils.UIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CompareViewModel(
    private val appRepository: AppRepositoryContract,
) : ViewModel() {

    private val _pokemonDetailOne = MutableStateFlow<Event<UIState<PokemonDetail>>>(
        Event(UIState.Initial)
    )

    val pokemonDetailOne = _pokemonDetailOne.asStateFlow()

    private val _pokemonDetailTwo = MutableStateFlow<Event<UIState<PokemonDetail>>>(
        Event(UIState.Initial)
    )

    val pokemonDetailTwo = _pokemonDetailTwo.asStateFlow()

    var selectedPokemonOneName = MutableStateFlow<String?>(null)
        private set

    var selectedPokemonTwoName = MutableStateFlow<String?>(null)
        private set

    val comparePokemonResult: Flow<ComparePokemonData?> =
        combine(_pokemonDetailOne, _pokemonDetailTwo) { source, target ->
            val sourceData = source.getContent()
            val targetData = target.getContent()

            if (sourceData is UIState.Success && targetData is UIState.Success) {

                val sourcePokemon = sourceData.data
                val targetPokemon = targetData.data

                val (sourceLabel, sourceValue) = sourcePokemon.mapToPokemonState()
                val (targetLabel, targetValue) = targetPokemon.mapToPokemonState()

                ComparePokemonData(
                    source = PokemonStateItem(
                        sourcePokemon.name, sourceLabel, sourceValue,
                    ),

                    target = PokemonStateItem(
                        targetPokemon.name, targetLabel, targetValue,
                    )
                )
            } else {
                null
            }
        }

    fun getPokemonDetailOne(q: String) {
        viewModelScope.launch {
            _pokemonDetailOne.value = Event(UIState.Loading)

            appRepository.getPokemonDetail(q).let {
                when (it) {
                    is ResultData.Success -> {
                        selectedPokemonOneName.value = q
                        _pokemonDetailOne.value = Event(UIState.Success(it.data!!))
                    }

                    is ResultData.Error -> {
                        _pokemonDetailOne.value = Event(UIState.Error(it.message!!))
                    }
                }
            }
        }
    }

    fun getPokemonDetailTwo(q: String) {
        viewModelScope.launch {
            _pokemonDetailTwo.value = Event(UIState.Loading)

            appRepository.getPokemonDetail(q).let {
                when (it) {
                    is ResultData.Success -> {
                        selectedPokemonTwoName.value = q
                        _pokemonDetailTwo.value = Event(UIState.Success(it.data!!))
                    }

                    is ResultData.Error -> {
                        _pokemonDetailTwo.value = Event(UIState.Error(it.message!!))
                    }
                }
            }
        }
    }

}