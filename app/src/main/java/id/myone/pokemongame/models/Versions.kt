package id.myone.pokemongame.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Versions(
    @SerializedName("generation-i")
    val generationI: GenerationI
)