package com.example.memovie.presentation.main

import com.example.memovie.core.data.model.GenreModel
import com.example.memovie.core.data.model.TmDbModel

data class MovieState(
    val loading: Boolean = false,
    val loadingTrendingMovies: Boolean = false,
    val loadingNextPage:Boolean = false,
    val listMovie: List<TmDbModel> = emptyList(),
    val listTrendingMovie: List<TmDbModel> = emptyList(),
    val errorMovie:String? = null,
    val errorTrendingMovie:String? = null,
    val currentPage: Int? = 1,
    val totalPages: Int? = 1,
)
data class TvState(
    val loading: Boolean = false,
    val loadingTrendingTV: Boolean = false,
    val loadingNextPage:Boolean = false,
    val listTvShow: List<TmDbModel> = emptyList(),
    val listTrendingTV: List<TmDbModel> = emptyList(),
    val errorTvShow:String? = null,
    val errorTrendingTv:String? = null,
    val currentPage: Int? = 1,
    val totalPages: Int? = 1,
)

data class GenreState(
    val loading: Boolean = false,
    val listGenre: List<GenreModel> = emptyList(),
    val errorGenre:String? = null,
    val selectedGenre: List<Int>? = null,
)