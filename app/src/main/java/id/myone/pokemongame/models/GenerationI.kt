package id.myone.pokemongame.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GenerationI(
    @SerializedName("red-blue")
    val redBlue: RedBlue,
    val yellow: Yellow
)