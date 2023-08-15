package id.myone.pokemongame.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AbilityResponse(
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>,
    val id: Int,
    val name: String
)


data class AbilityData(
    val flavorTextEntries: List<FlavorTextEntry>,
    val id: Int,
    val name: String
)