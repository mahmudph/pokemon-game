package id.myone.pokemongame.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DreamWorld(
    @SerializedName("front_default")
    val frontDefault: String?,
)