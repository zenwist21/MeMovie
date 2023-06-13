package com.example.memovie.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memovie.core.data.model.Resource
import com.example.memovie.core.data.model.VideoModel
import com.example.memovie.core.domain.repository.MovieRepository
import com.example.memovie.core.utils.NetworkConstant
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
class DetailActivityViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {
    private var _state = MutableStateFlow(DetailUIState())
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
                        getDetailMovie()
                    }

                    else -> {
                        getDetailTV()
                    }
                }
            }
        }
    }

    private fun getDetailMovie() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getDetailMovies(
                _state.value.itemId ?: 1
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _state.update { data ->
                            data.copy(
                                initialLoading = false,
                                errorResult = it.errorMessage.toString()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _state.update { data ->
                            data.copy(
                                initialLoading = true,
                                errorResult = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _state.update { data ->
                            data.copy(
                                initialLoading = false,
                                errorResult = null,
                                result = it.data
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }

    fun clearLink() {
        _state.update {
            it.copy(
                resultVideo = emptyList()
            )
        }
    }

    fun getMovieVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getMovieVideo(
                _state.value.itemId ?: 1
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _state.update { data ->
                            data.copy(
                                loadingMovies = false,
                                errorVideo = it.errorMessage.toString()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _state.update { data ->
                            data.copy(
                                loadingMovies = true,
                                errorVideo = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _state.update { data ->
                            data.copy(
                                loadingMovies = false,
                                errorVideo = null,
                                resultVideo = it.data?.results as List<VideoModel>
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }

    fun getTvVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTVVideo(
                _state.value.itemId ?: 1
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _state.update { data ->
                            data.copy(
                                loadingMovies = false,
                                errorVideo = it.errorMessage.toString()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _state.update { data ->
                            data.copy(
                                loadingMovies = true,
                                errorVideo = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _state.update { data ->
                            data.copy(
                                loadingMovies = false,
                                errorVideo = if ((it.data?.results as List<VideoModel>).isEmpty()) NetworkConstant.NOT_FOUND else null,
                                resultVideo = it.data.results as List<VideoModel>
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }

    private fun getDetailTV() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getDetailTV(
                _state.value.itemId ?: 1
            ).onEach {
                when (it) {
                    is Resource.DataError -> {
                        _state.update { data ->
                            data.copy(
                                initialLoading = false,
                                errorResult = it.errorMessage.toString()
                            )
                        }

                    }

                    is Resource.Loading -> {
                        _state.update { data ->
                            data.copy(
                                initialLoading = true,
                                errorResult = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        delay(1000)
                        _state.update { data ->
                            data.copy(
                                initialLoading = false,
                                errorResult = null,
                                result = it.data
                            )
                        }

                    }
                }
            }.launchIn(this)
        }
    }


}