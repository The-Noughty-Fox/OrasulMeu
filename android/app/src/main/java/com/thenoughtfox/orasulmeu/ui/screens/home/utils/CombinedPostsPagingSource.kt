package com.thenoughtfox.orasulmeu.ui.screens.home.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.openapitools.client.apis.PostsApi
import org.openapitools.client.models.PostDto
import retrofit2.HttpException
import java.io.IOException

class CombinedPostsPagingSource(private val api: PostsApi) {

    fun getPostsPagingSource(type: PostType, phrase: String = "", isAnonymous: Boolean) =
        PostsSource(type, phrase, isAnonymous)

    inner class PostsSource(
        private val postsType: PostType,
        private val phrase: String,
        private val isAnonymous: Boolean
    ) :
        PagingSource<Int, PostDto>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDto> {
            val position = params.key ?: 1
            return try {
                val response = when (postsType) {
                    PostType.POPULAR -> {
                        if (isAnonymous) {
                            api.getAllPostsOrderedByReactionsCountAnonymous(
                                position, params.loadSize
                            )
                        } else {
                            api.getAllPostsOrderedByReactionsCount(position, params.loadSize)
                        }
                    }

                    PostType.NEW -> {
                        if (isAnonymous) {
                            api.getAllPostsAnonymous(position, params.loadSize)
                        } else {
                            api.getAllPosts(position, params.loadSize)
                        }
                    }

                    PostType.SEARCH -> {
                        if (phrase.isNotEmpty()) {
                            if (isAnonymous) {
                                api.getPostsByPhraseAnonymous(position, params.loadSize, phrase)
                            } else {
                                api.getPostsByPhrase(position, params.loadSize, phrase)
                            }
                        } else {
                            return LoadResult.Page(emptyList(), null, null)
                        }
                    }

                    PostType.MY -> api.getMyPosts(position, params.loadSize)
                }

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

enum class PostType {
    POPULAR, NEW, SEARCH, MY
}