package id.myone.pokemongame.ui.detail.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import id.myone.pokemongame.databinding.FragmentAbilityTabBinding
import id.myone.pokemongame.ui.detail.adapter.AbilityDescListAdapter


class AbilityTabFragment : Fragment() {
    private lateinit var binding: FragmentAbilityTabBinding
    private lateinit var abilityDescListAdapter: AbilityDescListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAbilityTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAbilityDescListView()
    }

    private fun setupAbilityDescListView() {
        val abilitiesDesc = arguments?.getStringArrayList(ABILITIES_DESC_KEY)?: emptyList<String>()
        abilityDescListAdapter = AbilityDescListAdapter(abilitiesDesc)

        binding.apply {
            abilityRecyclerView.adapter = abilityDescListAdapter
            abilityRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

    }

    companion object {
        private const val ABILITIES_DESC_KEY = "abilities_desc_key"

        @JvmStatic
        fun newInstance(abilitiesDesc: List<String>) = AbilityTabFragment().apply {
            arguments = bundleOf(ABILITIES_DESC_KEY to abilitiesDesc)
        }
    }
}