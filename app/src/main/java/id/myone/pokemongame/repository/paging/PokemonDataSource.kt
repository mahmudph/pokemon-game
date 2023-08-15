/**
 * Created by Mahmud on 14/08/23.
 * mahmud120398@gmail.com
 */

package id.myone.pokemongame.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.myone.pokemongame.models.PokemonData
import id.myone.pokemongame.repository.network.ApiService

class PokemonDataSource(
    private val apiSource: ApiService,
) : PagingSource<Int, PokemonData>() {

    override fun getRefreshKey(state: PagingState<Int, PokemonData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonData> {
        return try {

            val position = params.key ?: INITIAL_PAGE
            val response = apiSource.getPokemonList(position, params.loadSize)

            LoadResult.Page(
                data = response.results,
                prevKey = if (position == INITIAL_PAGE) null else position - 1,
                nextKey = if (response.results.isEmpty()) null else position + 1,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}