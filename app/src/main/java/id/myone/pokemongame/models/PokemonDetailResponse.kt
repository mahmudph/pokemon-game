package id.myone.pokemongame.models

import androidx.annotation.Keep


@Keep
data class PokemonDetailResponse(
    val height: Int,
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val stats: List<Stat>,
    val types: List<Type>,
    val weight: Int,
    val abilities: List<Ability>
)


@Keep
data class PokemonDetail (
    val height: Int,
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val stats: List<Stat>,
    val types: List<Type>,
    val weight: Int,
    val abilities: List<AbilityData>
)