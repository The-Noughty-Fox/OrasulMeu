package com.thenoughtfox.orasulmeu.ui.screens.home.post_list.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.PostDto
import retrofit2.HttpException
import java.io.IOException

class CombinedPostsPagingSource(private val api: PostsApi) {

    val popularPostsPagingSource = PopularPostsPagingSource()
    val newPostsPagingSource = NewPostsPagingSource()

    inner class PopularPostsPagingSource : PagingSource<Int, PostDto>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDto> {
            val position = params.key ?: 1
            return try {
                val response = api.getAllPostsOrderedByReactionsCount(position, params.loadSize)
                val posts = response.body()?.data ?: emptyList()
                LoadResult.Page(
                    data = posts,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = if (posts.isEmpty()) null else position + 1
                )
            } catch (e: IOException) {
                LoadResult.Error(e)
            } catch (e: HttpException) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, PostDto>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
            }
        }
    }

    inner class NewPostsPagingSource : PagingSource<Int, PostDto>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDto> {
            val position = params.key ?: 1
            return try {
                val response = api.getAllPosts(position, params.loadSize)
                val posts = response.body()?.data ?: emptyList()
                LoadResult.Page(
                    data = posts,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = if (posts.isEmpty()) null else position + 1
                )
            } catch (e: IOException) {
                LoadResult.Error(e)
            } catch (e: HttpException) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, PostDto>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
            }
        }
    }
}