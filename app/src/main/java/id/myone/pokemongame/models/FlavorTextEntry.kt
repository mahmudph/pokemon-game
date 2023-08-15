package id.myone.pokemongame.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FlavorTextEntry(
    @SerializedName("flavor_text")
    val flavorText: String,
    val language: Language,
)

@Keep
data class Language(
    val name: String,
    val url: String,
)