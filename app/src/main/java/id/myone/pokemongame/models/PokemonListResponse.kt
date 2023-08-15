package id.myone.pokemongame.models

import androidx.annotation.Keep

@Keep
data class PokemonListResponse(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<PokemonData>
)

@Keep
data class PokemonData(
    val name: String,
    val url: String
)