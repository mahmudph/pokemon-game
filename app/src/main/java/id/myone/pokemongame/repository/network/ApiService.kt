/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.repository.network

import id.myone.pokemongame.models.AbilityResponse
import id.myone.pokemongame.models.PokemonDetailResponse
import id.myone.pokemongame.models.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PokemonListResponse

    @GET("pokemon/{q}")
    suspend fun getPokemonDetail(@Path("q") q: String): PokemonDetailResponse

    @GET("ability/{q}")
    suspend fun getPokemonAbility(@Path("q") q: String): AbilityResponse
}