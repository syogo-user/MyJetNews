package com.example.myjetnews.data.posts

import com.example.myjetnews.model.Post
import com.example.myjetnews.model.PostsFeed
import kotlinx.coroutines.flow.Flow
import com.example.myjetnews.data.Result
interface PostsRepository {
    suspend fun getPost(postId: String?): Result<Post>
    suspend fun getPostsFeed(): Result<PostsFeed>
    fun observeFavorites(): Flow<Set<String>>
    suspend fun toggleFavorite(postId: String)
}