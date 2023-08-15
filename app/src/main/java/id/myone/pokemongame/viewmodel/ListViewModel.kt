/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.myone.pokemongame.repository.AppRepositoryContract
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ListViewModel(
    repositoryContract: AppRepositoryContract,
): ViewModel() {
    val getPokemonList = repositoryContract.getPokemonList().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty()
    ).cachedIn(viewModelScope)
}