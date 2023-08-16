package id.myone.pokemongame.ui.utils.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import id.myone.pokemongame.R
import id.myone.pokemongame.databinding.FragmentLoadingBinding

class LoadingFragment : DialogFragment() {
    private lateinit var binding: FragmentLoadingBinding

    private val cancelable by lazy {
        arguments?.getBoolean(IS_CANCELABLE_KEY, true) ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            setCancelable(cancelable)
            window?.setLayout((resources.displayMetrics.widthPixels * 0.7).toInt(), 350)
            window?.setBackgroundDrawableResource(R.drawable.loading_shape)
        }
    }

    companion object {
        private const val IS_CANCELABLE_KEY = "IS_CANCELABLE_KEY"

        @JvmStatic
        fun newInstance(cancelable: Boolean = true) = LoadingFragment().apply {
            arguments = bundleOf(IS_CANCELABLE_KEY to cancelable)
        }
    }
}