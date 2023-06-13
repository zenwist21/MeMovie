package com.example.memovie.presentation.main.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memovie.core.data.model.GenreModel
import com.example.memovie.core.data.model.Resource
import com.example.memovie.core.data.model.TmDbModel
import com.example.memovie.core.domain.repository.MovieRepository
import com.example.memovie.core.utils.NetworkConstant
import com.example.memovie.presentation.components.params.MovieParams
import com.example.memovie.presentation.main.GenreState
import com.example.memovie.presentation.main.MovieState
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
class MovieViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {
    private var _movieState = MutableStateFlow(MovieState())
    private var _genreState = MutableStateFlow(GenreState())
    val movieState get() = _movieState.asStateFlow()
    val genreState get() = _genreState.asStateFlow()

    init {
        execute()
    }

    fun execute() = viewModelScope.launch(Dispatchers.IO) {
        val list = async { getMovieGenres() }
        val movie = async { getMoviesList() }
        val movieTrending = async { getTrendingMovies() }
        list.await()
        movie.await()
        movieTrending.await()
    }

    fun loadMoreActivities() {
        if ((_movieState.value.currentPage ?: 1) <= (_movieState.value.totalPages ?: 1)) {
            val nextPage = (_movieState.value.currentPage ?: 1) + 1
            getMoviesList(nextPage)
        }
    }

    fun getMoviesList(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getMovieList(
                MovieParams.getMovieParams(
                    page = page,
                    genres = if (_genreState.value.selectedGenre != null) _genreState.value.selectedGenre else null
                )
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _movieState.update { data ->
                            data.copy(
                                loading = false,
                                errorMovie = it.errorMessage.toString(),
                                listMovie = emptyList()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _movieState.update { data ->
                            data.copy(
                                loading = page == 1,
                                loadingNextPage = page != 1,
                                listMovie = if (page != 1) {
                                    _movieState.value.listMovie + listOf(TmDbModel(title = DUMMY))
                                } else {
                                    getDummyMovie()
                                },
                                errorMovie = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _movieState.update { data ->
                            data.copy(
                                loading = false,
                                errorMovie = null,
                                loadingNextPage = false,
                                currentPage = page,
                                listMovie = if (page != 1) {
                                    val temp = data.listMovie as MutableList<TmDbModel>
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
                        _movieState.update { data ->
                            data.copy(
                                loading = false,
                                errorMovie = it.errorMessage.toString(),
                                listMovie = emptyList()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _movieState.update { data ->
                            data.copy(
                                loading = true,
                                listMovie = getDummyMovie(),
                                errorMovie = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _movieState.update { data ->
                            data.copy(
                                loading = false,
                                errorMovie = if (it.data?.results.isNullOrEmpty()) NetworkConstant.NOT_FOUND else null,
                                currentPage = 1,
                                listMovie = it.data?.results ?: listOf(),
                                totalPages = 1
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }

    private fun getTrendingMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTrendingMovieList().onEach {
                when (it) {
                    is Resource.DataError -> {
                        _movieState.update { data ->
                            data.copy(
                                loadingTrendingMovies = false,
                                errorTrendingMovie = it.errorMessage.toString()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _movieState.update { data ->
                            data.copy(
                                loadingTrendingMovies = true,
                                listTrendingMovie = getDummyMovie(),
                                errorTrendingMovie = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _movieState.update { data ->
                            data.copy(
                                loadingTrendingMovies = false,
                                errorTrendingMovie = if (it.data?.results.isNullOrEmpty()) NetworkConstant.NOT_FOUND else null,
                                listTrendingMovie = it.data?.results ?: emptyList()
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