/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.di

import id.myone.pokemongame.BuildConfig
import id.myone.pokemongame.repository.AppRepository
import id.myone.pokemongame.repository.AppRepositoryContract
import id.myone.pokemongame.repository.network.ApiService
import id.myone.pokemongame.utils.ImageProcessing
import id.myone.pokemongame.viewmodel.CompareViewModel
import id.myone.pokemongame.viewmodel.DetailViewModel
import id.myone.pokemongame.viewmodel.ListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    single<AppRepositoryContract> { AppRepository(get()) }
}

val utilityModule = module {
    single { ImageProcessing() }
}

val viewModelModule = module {
    viewModel { ListViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { CompareViewModel(get()) }
}