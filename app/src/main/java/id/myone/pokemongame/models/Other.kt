package id.myone.pokemongame.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Other(
    @SerializedName("dream_world")
    val dreamWorld: DreamWorld
)