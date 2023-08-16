package id.myone.pokemongame

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import id.myone.pokemongame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val navigationListener = NavController.OnDestinationChangedListener { _, dest, _ ->
        binding.apply {
            bottomAppBar.visibility = when (dest.id) {
                R.id.detail -> View.GONE
                else -> View.VISIBLE
            }

            toolbar.apply {
                title = when (dest.id) {
                    R.id.detail -> "Detail"
                    R.id.compare -> "Compare Character"
                    else -> "PokeApp - Mahmud"
                }

                if (R.id.detail == dest.id) {
                    if (appBarLayout.isLifted) appBarLayout.setExpanded(false)
                    setNavigationIcon(R.drawable.baseline_arrow_back_24)
                } else {
                    navigationIcon = null
                }
            }
        }
    }

    private val onClickListener = View.OnClickListener {
        navController.navigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setUpView()
    }

    private fun setUpView() {

        val navHostFragment = supportFragmentManager.findFragmentById(
            binding.container.id
        ) as NavHostFragment

        navController = navHostFragment.findNavController()
        binding.toolbar.setNavigationOnClickListener(onClickListener)

        navController.addOnDestinationChangedListener(navigationListener)
        binding.bottomNav.setupWithNavController(navController)
    }
}