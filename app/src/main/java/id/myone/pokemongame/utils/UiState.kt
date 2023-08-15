/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.utils

sealed class UIState<out T>{
    data object Initial: UIState<Nothing>()
    data object Loading: UIState<Nothing>()
    data class Success<T>(val data: T): UIState<T>()
    data class Error(val message: String): UIState<Nothing>()
}