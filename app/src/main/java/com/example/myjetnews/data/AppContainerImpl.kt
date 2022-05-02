package com.example.myjetnews.data

import android.content.Context
import com.example.myjetnews.data.interests.InterestsRepository
import com.example.myjetnews.data.posts.PostsRepository
import com.example.myjetnews.data.posts.impl.FakeInterestsRepository
import com.example.myjetnews.data.posts.impl.FakePostsRepository

interface AppContainer {
    val postsRepository: PostsRepository
    val interestsRepository: InterestsRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    override val postsRepository: PostsRepository by lazy {
        FakePostsRepository()
    }

    override val interestsRepository: InterestsRepository by lazy {
        FakeInterestsRepository()
    }
}