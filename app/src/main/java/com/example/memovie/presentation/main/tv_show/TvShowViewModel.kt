package com.example.memovie.presentation.main.tv_show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memovie.core.data.model.GenreModel
import com.example.memovie.core.data.model.Resource
import com.example.memovie.core.data.model.TmDbModel
import com.example.memovie.core.domain.repository.MovieRepository
import com.example.memovie.core.utils.NetworkConstant
import com.example.memovie.presentation.components.params.MovieParams
import com.example.memovie.presentation.main.GenreState
import com.example.memovie.presentation.main.TvState
import com.example.memovie.presentation.utils.DUMMY
import com.example.memovie.presentation.utils.getDummyMovie
import com.example.memovie.presentation.utils.setGenre
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {
    private var _tvState = MutableStateFlow(TvState())
    private var _genreState = MutableStateFlow(GenreState())
    val tvState get() = _tvState.asStateFlow()
    val genreState get() = _genreState.asStateFlow()

    init {
        execute()
    }

    fun execute() = viewModelScope.launch(Dispatchers.IO) {
        val list = async { getMovieGenres() }
        val tvShow = async { getTvShowList() }
        val tvTrending = async { getTrendingTv() }
        list.await()
        tvShow.await()
        tvTrending.await()
    }

    fun loadMoreActivities() {
        if ((_tvState.value.currentPage ?: 1) <= (_tvState.value.totalPages ?: 1)) {
            val nextPage = (_tvState.value.currentPage ?: 1) + 1
            getTvShowList(nextPage)
        }
    }

    fun getTvShowList(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTvList(
                MovieParams.getMovieParams(
                    page = page,
                    genres = if (_genreState.value.selectedGenre != null) _genreState.value.selectedGenre else null
                )
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _tvState.update { data ->
                            data.copy(
                                loading = false,
                                errorTvShow = it.errorMessage.toString(),
                                listTvShow = emptyList()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _tvState.update { data ->
                            data.copy(
                                loading = page == 1,
                                loadingNextPage = page != 1,
                                listTvShow = if (page != 1) {
                                    _tvState.value.listTvShow + listOf(TmDbModel(title = DUMMY))
                                } else {
                                    getDummyMovie()
                                },
                                errorTvShow = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _tvState.update { data ->
                            data.copy(
                                loading = false,
                                errorTvShow = null,
                                loadingNextPage = false,
                                currentPage = page,
                                listTvShow = if (page != 1) {
                                    val temp = data.listTvShow as MutableList<TmDbModel>
                                    temp.remove(temp.find { dTemp -> dTemp == TmDbModel(title = DUMMY) })
                                    temp.addAll(it.data?.results ?: mutableListOf())
                                    temp
                                } else (it.data?.results as List<TmDbModel>),
                                totalPages = it.data?.totalPages
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }

    fun searchMovie(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getSearched(
                MovieParams.getSearchParam(
                    query = query,
                    genres = if (_genreState.value.selectedGenre != null) _genreState.value.selectedGenre else null
                )
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _tvState.update { data ->
                            data.copy(
                                loading = false,
                                errorTvShow = it.errorMessage.toString(),
                                listTvShow = emptyList()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _tvState.update { data ->
                            data.copy(
                                loading = true,
                                listTvShow = getDummyMovie(),
                                errorTvShow = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _tvState.update { data ->
                            data.copy(
                                loading = false,
                                errorTvShow = if (it.data?.results.isNullOrEmpty()) NetworkConstant.NOT_FOUND else null,
                                currentPage = 1,
                                listTvShow = it.data?.results ?: listOf(),
                                totalPages = 1
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }

    private fun getTrendingTv() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTrendingTvList().onEach {
                when (it) {
                    is Resource.DataError -> {
                        _tvState.update { data ->
                            data.copy(
                                loadingTrendingTV = false,
                                errorTrendingTv = it.errorMessage.toString()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _tvState.update { data ->
                            data.copy(
                                loadingTrendingTV = true,
                                listTrendingTV = getDummyMovie(),
                                errorTrendingTv = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _tvState.update { data ->
                            data.copy(
                                loadingTrendingTV = false,
                                errorTrendingTv = if (it.data?.results.isNullOrEmpty()) NetworkConstant.NOT_FOUND else null,
                                listTrendingTV = it.data?.results ?: emptyList()
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }

    private fun getMovieGenres() = viewModelScope.launch(Dispatchers.IO) {
        repo.getMovieGenres().onEach {
            when (it) {
                is Resource.DataError -> {
                    _genreState.update { data ->
                        data.copy(
                            loading = false,
                            errorGenre = it.errorMessage.toString()
                        )
                    }
                }

                is Resource.Loading -> {
                    _genreState.update { data ->
                        data.copy(
                            loading = true,
                            selectedGenre = null,
                            errorGenre = null
                        )
                    }
                }

                is Resource.Success -> {
                    delay(1000)
                    _genreState.update { data ->
                        data.copy(
                            loading = false,
                            errorGenre = null,
                            listGenre = it.data as List<GenreModel>,
                            selectedGenre = null
                        )
                    }
                }
            }
        }.launchIn(this)
    }

    fun updateGenreState(data: GenreState) = _genreState.update { data }
    fun setOrRemoveSelectedPosition(data: GenreModel) {
        val listSelected = mutableListOf<GenreModel>()
        val temp = mutableListOf<GenreModel>()
        listSelected.addAll(_genreState.value.listGenre.filter { it.isSelected })
        temp.addAll(_genreState.value.listGenre.filter { !it.isSelected })
        if (listSelected.isNotEmpty()) {
            if (listSelected.contains(data)) {
                listSelected.remove(data)
                temp.add(data.copy(isSelected = false))
            } else {
                listSelected.add(data.copy(isSelected = true))
                temp.remove(data)
            }
        } else {
            listSelected.add(data.copy(isSelected = true))
            temp.remove(data)
        }
        updateGenreState(
            _genreState.value.copy(
                listGenre = listSelected + temp,
                selectedGenre = setGenre(listSelected + temp)
            )
        )
    }
}