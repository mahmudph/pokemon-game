package id.myone.pokemongame.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Stat(
    @SerializedName("base_stat")
    val baseStat: Int,
    val effort: Int,
    val stat: StatX
)