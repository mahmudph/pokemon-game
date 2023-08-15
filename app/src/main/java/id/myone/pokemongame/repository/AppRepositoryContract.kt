/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.repository

import androidx.paging.PagingData
import id.myone.pokemongame.models.PokemonData
import id.myone.pokemongame.models.PokemonDetail
import kotlinx.coroutines.flow.Flow

interface AppRepositoryContract {
    fun getPokemonList(): Flow<PagingData<PokemonData>>
    suspend fun getPokemonDetail(q: String): ResultData<PokemonDetail>
}