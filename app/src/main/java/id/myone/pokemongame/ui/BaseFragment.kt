/**
 * Created by Mahmud on 16/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import id.myone.pokemongame.ui.utils.loading.LoadingFragment
import id.myone.pokemongame.utils.Event
import id.myone.pokemongame.utils.ImageProcessing
import id.myone.pokemongame.utils.UIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

abstract class BaseFragment<B : ViewBinding> : Fragment(), BaseFragmentContract<B> {
    private var loadingDialog: LoadingFragment? = null

    private var _binding: B? = null
    protected val binding get() = _binding!!

    protected val imageProcessing by inject<ImageProcessing>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observableViewModel()
        setUpLoadingDialog()
        setUpView()
    }

    private fun setUpLoadingDialog() {
        loadingDialog = LoadingFragment.newInstance(false)
    }

    abstract override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): B


    abstract override fun setUpView()

    override fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        loadingDialog?.show(parentFragmentManager, LoadingFragment.javaClass.name)
    }

    override fun hideLoading() {
        loadingDialog?.dismiss()
    }

    override fun observableViewModel() {}

    override fun <T> observableData(
        flow: Flow<Event<UIState<T>>>,
        onSuccess: (T) -> Unit,
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { result ->

                    result.getContentIfNotHandled()?.let { state ->
                        when (state) {
                            is UIState.Loading -> showLoading()
                            is UIState.Success -> {
                                hideLoading()
                                onSuccess(state.data)
                            }

                            is UIState.Error -> {
                                hideLoading()
                                showSnackBar(state.message)
                            }

                            else -> {}
                        }
                    }
                    Log.i(this.javaClass.name, "receive data  : $result")
                }
            }
        }
    }

    override fun <T> observableSimpleData(flow: Flow<T>, onSuccess: (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { result -> onSuccess(result) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}