package id.myone.pokemongame.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Ability(
    val ability: AbilityX,
    @SerializedName("is_hidden")
    val isHidden: Boolean,
    val slot: Int
)