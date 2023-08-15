/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.myone.pokemongame.models.PokemonDetail
import id.myone.pokemongame.repository.ResultData
import id.myone.pokemongame.repository.AppRepositoryContract
import id.myone.pokemongame.utils.Event
import id.myone.pokemongame.utils.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: AppRepositoryContract,
): ViewModel() {

    private val _result = MutableStateFlow<Event<UIState<PokemonDetail>>>(Event(UIState.Initial))
    val result = _result.asStateFlow()

    fun loadPokemonByName(q: String) {
        viewModelScope.launch {

            _result.value =  Event(UIState.Loading)

            when (val result = repository.getPokemonDetail(q)) {
                is ResultData.Success -> {
                    _result.value = Event(UIState.Success(result.data!!))
                }
                is ResultData.Error -> {
                    _result.value = Event(UIState.Error(message = result.message!!))
                }
            }
        }
    }
}