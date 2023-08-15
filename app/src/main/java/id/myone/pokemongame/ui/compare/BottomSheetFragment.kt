package id.myone.pokemongame.ui.compare

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import id.myone.pokemongame.databinding.FragmentBottomSheetBinding
import id.myone.pokemongame.ui.list.adapter.PokemonListAdapter
import id.myone.pokemongame.ui.list.adapter.PokemonLoadAdapter
import id.myone.pokemongame.utils.ImageProcessing
import id.myone.pokemongame.viewmodel.ListViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var listener: OnSelectedPokemonListener

    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var adapter: PokemonListAdapter

    private val selectedPokemonKey by lazy {
        arguments?.getString(SELECTED_POKEMON_KEY)
    }

    private val selectedPokemonName by lazy {
        arguments?.getString(SELECTED_POKEMON_NAME_KEY)
    }

    private val viewModel by inject<ListViewModel>()
    private val imageProcessing by inject<ImageProcessing>()

    private val clickListener = object : PokemonListAdapter.OnClickListener {
        override fun click(q: String) {
            dismiss()
            Log.i(this.javaClass.name, "Selected Pokemon: $q")
            selectedPokemonKey?.let { listener.onSelectedPokemon(q, it) }
        }
    }

    fun setOnSelectedPokemonListener(listener: OnSelectedPokemonListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBottomSheetBehaviour()
        observablePokemonList()
        setUpAdapter()
        setUpView()
    }

    private fun setUpBottomSheetBehaviour() {
        val windowHeight = resources.displayMetrics.heightPixels
        val maxHeightWindow = (windowHeight * 0.9).toInt()

        (dialog as BottomSheetDialog).behavior.apply {
            isFitToContents = true
            maxHeight = maxHeightWindow
        }
    }

    private fun setUpView() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setUpAdapter() {
        adapter = PokemonListAdapter(requireContext(), imageProcessing, selectedPokemonName)
        adapter.setOnClickListener(clickListener)

        binding.pokemonBottomSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pokemonBottomSheetRecyclerView.adapter = adapter.withLoadStateFooter(
            footer = PokemonLoadAdapter { adapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadStates ->
                if (loadStates.refresh is LoadState.Error) {
                    Snackbar.make(binding.root, "Failure to get pokemon list", Snackbar.LENGTH_SHORT).show()
                }
                Log.i(this.javaClass.name, loadStates.toString())
            }
        }
    }

    private fun observablePokemonList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPokemonList.collect { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    interface OnSelectedPokemonListener {
        fun onSelectedPokemon(name: String, key: String)
    }

    companion object {
        private const val SELECTED_POKEMON_KEY = "selected_pokemon"
        private const val SELECTED_POKEMON_NAME_KEY = "selected_pokemon_name"

        @JvmStatic
        fun newInstance(key: String, selectedPokemonName: String? = null) =
            BottomSheetFragment().apply {
                arguments = bundleOf(
                    SELECTED_POKEMON_KEY to key,
                    SELECTED_POKEMON_NAME_KEY to selectedPokemonName,
                )
            }
    }
}