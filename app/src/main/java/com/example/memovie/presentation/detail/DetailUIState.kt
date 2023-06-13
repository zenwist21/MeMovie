package com.example.memovie.presentation.detail

import com.example.memovie.core.data.model.TmDbModel
import com.example.memovie.core.data.model.VideoModel

data class DetailUIState(
    val initialLoading: Boolean = false,
    val loadingMovies: Boolean = false,
    val result: TmDbModel? = null,
    val resultVideo:List<VideoModel> = emptyList(),
    val errorVideo:String? = null,
    val errorResult:String? = null,
    val itemId: Int? = null,
    val type: DetailType? = DetailType.MOVIE
)


enum class DetailType  {
    MOVIE,
    TV_SHOW
}