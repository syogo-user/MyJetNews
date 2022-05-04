package com.example.myjetnews.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myjetnews.R
import com.example.myjetnews.data.Result
import com.example.myjetnews.data.posts.PostsRepository
import com.example.myjetnews.model.Post
import com.example.myjetnews.model.PostsFeed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*


sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessage: List<ErrorMessage>
    val searchInput: String

    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessage: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState

    data class HasPosts(
        val postsFeed: PostsFeed,
        val selectedPost: Post,
        val isArticleOpen: Boolean,
        val favorites: Set<String>,
        override val isLoading: Boolean,
        override val errorMessage: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState
}

private data class HomeViewModelState(
    val postsFeed: PostsFeed? = null,
    val selectedPostsId: String? = null,
    val isArticleOpen: Boolean = false,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: List<ErrorMessage> = emptyList(),
    val searchInput: String = ""
) {
    fun toUiState(): HomeUiState =
        if (postsFeed == null) {
            HomeUiState.NoPosts(
                isLoading = isLoading,
                errorMessage = errorMessage,
                searchInput = searchInput
            )
        } else {
            HomeUiState.HasPosts(
                postsFeed = postsFeed,
                selectedPost = postsFeed.allPosts.find {
                    it.id == selectedPostsId
                } ?: postsFeed.highlightedPost,
                isArticleOpen = isArticleOpen,
                favorites = favorites,
                isLoading = isLoading,
                errorMessage = errorMessage,
                searchInput = searchInput
            )
        }
}

class HomeViewModel(
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshPosts()

        viewModelScope.launch {
            postsRepository.observeFavorites().collect { favorites ->
                viewModelState.update { it.copy(favorites = favorites) }
            }
        }
    }

   fun refreshPosts() {
       viewModelState.update { it.copy(isLoading = true)}

       viewModelScope.launch {
           val result = postsRepository.getPostsFeed()
           viewModelState.update {
               when(result) {
                   is Result.Success -> it.copy(postsFeed =  result.data, isLoading = false)
                   is Result.Error -> {
                       val errorMessages = it.errorMessages + ErrorMessage(
                           id = UUID.randomUUID().mostSignificantBits,
                           messageId = R.string.load_error
                       )
                       it.copy(errorMessage = errorMessages, isLoading = false)
                   }
               }
           }
       }
   }

    fun toggleFavorite(postId: String) {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId)
        }
    }

    fun selectArticle(postId: String) {
        interactedWithArticleDetails(postId)
    }

    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessage = currentUiState.errorMessage.filterNot { it.id == errorId}
            currentUiState.copy(errorMessage =  errorMessage)
        }
    }

    fun interactedWithFeed() {
        viewModelState.update {
            it.copy(isArticleOpen = false)
        }
    }


    fun interactedWithArticleDetails(postId: String) {
        viewModelState.update {
            it.copy(
                selectedPostsId = postId,
                isArticleOpen =  true
            )
        }
    }

    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }

    compoanion object {
        fun provideFactory(
            postsRepository: PostsRepository,
        ): ViewModelProvide.Factory = object : ViewModelProvider.Factory {
            
        }
    }










}





































