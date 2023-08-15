/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.utils

open class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if(!hasBeenHandled) {
            hasBeenHandled = true
            content
        } else null
    }

    fun getContent(): T = content
}