/**
 * Created by Mahmud on 16/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import id.myone.pokemongame.utils.Event
import id.myone.pokemongame.utils.UIState
import kotlinx.coroutines.flow.Flow

interface BaseFragmentContract<T> {
    fun createBinding(inflater: LayoutInflater, container: ViewGroup?): T
    fun <T> observableData(flow: Flow<Event<UIState<T>>>, onSuccess: (T) -> Unit)
    fun <T> observableSimpleData(flow: Flow<T>, onString: (T) -> Unit)

    fun observableViewModel()
    fun setUpView()

    fun showLoading()
    fun hideLoading()

    fun showSnackBar(message: String)
}