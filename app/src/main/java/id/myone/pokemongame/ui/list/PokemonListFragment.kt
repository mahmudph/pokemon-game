package id.myone.pokemongame.ui.list


import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.myone.pokemongame.R
import id.myone.pokemongame.databinding.PokemonListFragmentBinding
import id.myone.pokemongame.ui.BaseFragment
import id.myone.pokemongame.ui.list.adapter.PokemonListAdapter
import id.myone.pokemongame.ui.list.adapter.PokemonLoadAdapter
import id.myone.pokemongame.viewmodel.ListViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PokemonListFragment : BaseFragment<PokemonListFragmentBinding>() {

    private lateinit var adapter: PokemonListAdapter
    private val viewModel by inject<ListViewModel>()

    private val onClickListener = object : PokemonListAdapter.OnClickListener {
        override fun click(q: String) {
            val data = PokemonListFragmentDirections.actionHomeToDetail().apply {
                pokemonName = q
            }
            view?.findNavController()?.navigate(data)
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): PokemonListFragmentBinding {
        return PokemonListFragmentBinding.inflate(inflater, container, false)
    }

    private fun hideLoadingWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
        }, 1000)
    }

    override fun setUpView() {
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
                when (loadStates.refresh) {
                    is LoadState.Loading -> showLoading()
                    is LoadState.Error -> {

                        binding.pokemonListSwipeRefreshLayout.isRefreshing = false
                        binding.pokemonListRecyclerView.visibility = View.GONE
                        binding.pokemonListError.root.visibility = View.VISIBLE

                        showSnackBar(getString(R.string.fetch_data_pokemon_list_error))
                        hideLoadingWithDelay()
                    }
                    else -> {
                        binding.pokemonListRecyclerView.visibility = View.VISIBLE
                        binding.pokemonListError.root.visibility = View.GONE

                        binding.pokemonListSwipeRefreshLayout.isRefreshing = false
                        hideLoadingWithDelay()
                    }
                }
            }
        }

        binding.pokemonListSwipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }

    override fun observableViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPokemonList.collect { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }
}