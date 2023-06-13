package com.example.memovie.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memovie.core.data.model.Resource
import com.example.memovie.core.data.model.ReviewDataModel
import com.example.memovie.core.domain.repository.MovieRepository
import com.example.memovie.presentation.components.params.MovieParams
import com.example.memovie.presentation.detail.DetailType
import com.example.memovie.presentation.utils.DUMMY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {

    private var _state = MutableStateFlow(ReviewUiState())
    val state get() = _state.asStateFlow()

    fun setMovieId(id: Int?, type: DetailType = DetailType.MOVIE) {
        _state.update {
            it.copy(itemId = id, type = type)
        }
    }

    fun getData() {
        state.value.let {
            if (it.itemId != 0) {
                when (it.type) {
                    DetailType.MOVIE -> {
                        getReviewMovie()
                    }

                    else -> {
                        getReviewTv()
                    }
                }
            }
        }
    }

    fun loadMoreActivities() {
        if (state.value.currentPage <= state.value.totalPages) {
            val nextPage = state.value.currentPage + 1
            _state.update {
                it.copy(currentPage = it.currentPage + 1)
            }
            when (state.value.type) {
                DetailType.MOVIE -> {
                    getReviewMovie(nextPage)
                }

                else -> {
                    getReviewTv(nextPage)
                }
            }
        }
    }


    private fun getReviewMovie(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getReviewMovie(
                MovieParams.getReview(
                    page = page,
                    movieId = state.value.itemId
                )
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _state.update { data ->
                            data.copy(
                                initialLoading = false,
                                loadingNextPage = false,
                                error = it.errorMessage.toString()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _state.update { data ->
                            data.copy(
                                initialLoading = page == 1,
                                loadingNextPage = page != 1,
                                listReview = if (page != 1) {
                                    state.value.listReview + listOf(ReviewDataModel(author = DUMMY))
                                } else {
                                    listOf(ReviewDataModel())
                                },
                                error = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _state.update { data ->
                            data.copy(
                                currentPage = page,
                                totalPages = it.data?.totalPages ?: 1,
                                initialLoading = false,
                                error = null,
                                loadingNextPage = false,
                                listReview = if (page != 1) {
                                    val temp = mutableListOf<ReviewDataModel>()
                                    temp.addAll(data.listReview)
                                    temp.remove(temp.find { dTemp -> dTemp == ReviewDataModel(author = DUMMY) })
                                    temp + (it.data?.results as List<ReviewDataModel>)
                                } else {
                                    (it.data?.results as List<ReviewDataModel>)
                                },
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }

    private fun getReviewTv(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getReviewTv(
                MovieParams.getReview(
                    page = page,
                    movieId = state.value.itemId
                )
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _state.update { data ->
                            data.copy(
                                initialLoading = false,
                                loadingNextPage = false,
                                listReview = emptyList(),
                                error = it.errorMessage.toString()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _state.update { data ->
                            data.copy(
                                initialLoading = page == 1,
                                loadingNextPage = page != 1,
                                listReview = if (page != 1) {
                                    state.value.listReview + listOf(ReviewDataModel(author = DUMMY))
                                } else {
                                    emptyList()
                                },
                                error = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _state.update { data ->
                            data.copy(
                                currentPage = page,
                                totalPages = it.data?.totalPages ?: 1,
                                initialLoading = false,
                                error = null,
                                loadingNextPage = false,
                                listReview = if (page != 1) {
                                    val temp = data.listReview as MutableList<ReviewDataModel>
                                    temp.remove(temp.find { dTemp -> dTemp == ReviewDataModel(author = DUMMY) })
                                    temp.addAll(it.data?.results ?: mutableListOf())
                                    temp
                                } else  (it.data?.results as List<ReviewDataModel>)
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }
}