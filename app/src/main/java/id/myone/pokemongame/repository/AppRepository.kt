/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.myone.pokemongame.BuildConfig
import id.myone.pokemongame.extensions.toAbilityData
import id.myone.pokemongame.extensions.toPokemonDetail
import id.myone.pokemongame.models.PokemonData
import id.myone.pokemongame.models.PokemonDetail
import id.myone.pokemongame.repository.network.ApiService
import id.myone.pokemongame.repository.paging.PokemonDataSource
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val apiService: ApiService,
) : AppRepositoryContract {
    override fun getPokemonList(): Flow<PagingData<PokemonData>> {
        return Pager(
            config = PagingConfig(
                pageSize = BuildConfig.PAGE_SIZE,
            ),
            pagingSourceFactory = { PokemonDataSource(apiService) }
        ).flow
    }

    override suspend fun getPokemonDetail(q: String): ResultData<PokemonDetail> {
        return try {

            val response = apiService.getPokemonDetail(q)
            val abilities = response.abilities.map {
                val abilityRes = apiService.getPokemonAbility(it.ability.name)
                abilityRes.toAbilityData()
            }

            ResultData.Success(response.toPokemonDetail(abilities))

        } catch (e: Exception) {
            e.printStackTrace()
            ResultData.Error(errorMessage = e.localizedMessage)
        }
    }
}
