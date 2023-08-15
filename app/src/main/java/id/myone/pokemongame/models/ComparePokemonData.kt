package id.myone.pokemongame.models


data class ComparePokemonData(
    val target: PokemonStateItem,
    val source: PokemonStateItem
)

data class PokemonStateItem(
    val name: String,
    val label: ArrayList<String>,
    val value: ArrayList<Int>,
)