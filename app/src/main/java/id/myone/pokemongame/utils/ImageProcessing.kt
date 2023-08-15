/**
 * Created by Mahmud on 15/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.utils

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest


class ImageProcessing {

    fun loadImage(context: Context, pokemonImageUri: String, imageView: ImageView) {
        val loader = ImageLoader.Builder(context)
            .crossfade(true)
            .components { add(SvgDecoder.Factory()) }
            .build()

        val imageReq = ImageRequest.Builder(context)
            .data(pokemonImageUri)
            .crossfade(true)
            .target(imageView)
            .build()
        loader.enqueue(imageReq)
    }
}