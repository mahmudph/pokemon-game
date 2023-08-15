/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.extensions

import id.myone.pokemongame.common.pokemonStateTypes
import id.myone.pokemongame.models.AbilityData
import id.myone.pokemongame.models.AbilityResponse
import id.myone.pokemongame.models.PokemonDetail
import id.myone.pokemongame.models.PokemonDetailResponse


fun String.getIdFromUrl(): String {
    val split = this.split("/")
    return split[split.size - 2]
}

fun String.ucFirst(): String {
    return if (isNotEmpty()) {
        this[0].uppercaseChar() + substring(1)
    } else {
        this
    }
}

fun AbilityResponse.toAbilityData(): AbilityData {
    return AbilityData(
        id = this.id,
        name = this.name,
        flavorTextEntries = this.flavorTextEntries.filter {
            it.language.name == "en"
        }
    )
}

fun PokemonDetailResponse.toPokemonDetail(abilitiesData: List<AbilityData>): PokemonDetail {
    return PokemonDetail(
        id = this.id,
        name = this.name,
        height = this.height,
        weight = this.weight,
        abilities = abilitiesData,
        sprites = this.sprites,
        stats = this.stats,
        types = this.types,
    )
}

fun PokemonDetail.mapToPokemonState(): Pair<ArrayList<String>, ArrayList<Int>> {
    val labels = mutableListOf<String>()
    val data = mutableListOf<Int>()

    stats.forEach {
        if (it.stat.name !in pokemonStateTypes) return@forEach

        labels.add(it.stat.name)
        data.add(it.baseStat)
    }

    return Pair(ArrayList(labels), ArrayList(data))
}