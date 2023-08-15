package id.myone.pokemongame.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.myone.pokemongame.databinding.PokemonListFragmentBinding
import id.myone.pokemongame.ui.list.adapter.PokemonListAdapter
import id.myone.pokemongame.ui.list.adapter.PokemonLoadAdapter
import id.myone.pokemongame.utils.ImageProcessing
import id.myone.pokemongame.viewmodel.ListViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PokemonListFragment : Fragment() {
    private lateinit var binding: PokemonListFragmentBinding
    private lateinit var adapter: PokemonListAdapter

    private val viewModel by inject<ListViewModel>()
    private val imageProcessing by inject<ImageProcessing>()

    private val onClickListener = object : PokemonListAdapter.OnClickListener {
        override fun click(q: String) {
            val data = PokemonListFragmentDirections.actionHomeToDetail().apply {
                pokemonName = q
            }
            view?.findNavController()?.navigate(data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = PokemonListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListViewAdapter()
        observablePokemonList()
        provideRefreshPokemonList()
    }

    private fun setUpListViewAdapter() {
        adapter = PokemonListAdapter(requireContext(), imageProcessing)
        binding.pokemonListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pokemonListRecyclerView.adapter = adapter.withLoadStateFooter(
            footer = PokemonLoadAdapter {
                adapter.retry()
            }
        )

        adapter.setOnClickListener(onClickListener)

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadStates ->
                if (loadStates.refresh is LoadState.Error) {
                    binding.pokemonListRecyclerView.visibility = View.GONE
                    binding.pokemonListError.root.visibility = View.VISIBLE
                } else {
                    binding.pokemonListRecyclerView.visibility = View.VISIBLE
                    binding.pokemonListError.root.visibility = View.GONE
                    binding.pokemonListSwipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun provideRefreshPokemonList() {
        binding.pokemonListSwipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
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
}