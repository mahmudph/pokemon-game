/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.repository.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.myone.pokemongame.BuildConfig
import id.myone.pokemongame.models.PokemonData
import id.myone.pokemongame.repository.network.ApiService

class PokemonDataSource(
    private val apiSource: ApiService,
) : PagingSource<Int, PokemonData>() {

    override fun getRefreshKey(state: PagingState<Int, PokemonData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(BuildConfig.PAGE_SIZE) ?: anchorPage?.nextKey?.minus(BuildConfig.PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonData> {
        return try {

            val position = params.key ?: INITIAL_PAGE

            Log.i(TAG, "load: $position")
            val response = apiSource.getPokemonList(position, params.loadSize)

            LoadResult.Page(
                data = response.results,
                prevKey = if (position == INITIAL_PAGE) null else position - BuildConfig.PAGE_SIZE,
                nextKey = if (response.results.isEmpty()) null else position + BuildConfig.PAGE_SIZE,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val TAG = "PokemonDataSource"
        private const val INITIAL_PAGE = 0
    }
}