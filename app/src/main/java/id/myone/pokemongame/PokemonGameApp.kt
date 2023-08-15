/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame

import android.app.Application
import id.myone.pokemongame.di.networkModule
import id.myone.pokemongame.di.utilityModule
import id.myone.pokemongame.di.viewModelModule
import org.koin.core.context.startKoin

class PokemonGameApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            applicationContext
            modules(networkModule, viewModelModule, utilityModule)
        }
    }
}