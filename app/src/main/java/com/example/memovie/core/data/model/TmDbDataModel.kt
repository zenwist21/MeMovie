package com.example.memovie.core.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TmDbModel(
    val adult: Boolean = false,
    val backdropPath: String? = null,
    val budget: Int? = 0,
    val genres: List<GenreModel>? = emptyList(),
    val homepage: String? = null,
    val id: Int? = 0,
    val imdbId: String? = null,
    val originalLanguage: String? = null,
    val firstAirDate: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = 0.0,
    val posterPath: String? = null,
    val productionCompanies: List<ProductionCompanyModel>? = emptyList(),
    val productionCountries: List<ProductionCountryModel>? = emptyList(),
    val releaseDate: String? = null,
    val revenue: Long? = 0,
    val runtime: Int? = 0,
    val spokenLanguages: List<SpokenLanguageModel>? = emptyList(),
    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val name: String? = null,
    val originalName: String? = null,
    val video: Boolean = false,
    val voteAverage: Double? = 0.0,
    val voteCount: Int? = 0,
    val message: String? = "",
    val totalPage: Int = 1,
) : Parcelable

@Parcelize
data class ProductionCompanyModel(
    val id: Int? = 0,
    val logo_path: String? = null,
    val name: String? = null,
    val origin_country: String? = null
) : Parcelable

@Parcelize
data class ProductionCountryModel(
    val iso_3166_1: String? = null,
    val name: String? = null
) : Parcelable

@Parcelize
data class GenreModel(
    val id: Int? = 0,
    val name: String? = null,
    val message: String? = null,
    var isSelected: Boolean = false,
) : Parcelable{

}

@Parcelize
data class VideoModel(
    val id: String? = null,
    val iso3166: String? = null,
    val iso6391: String? = null,
    val key: String? = null,
    val name: String? = null,
    val official: Boolean = false,
    val publishedAt: String? = null,
    val site: String? = null,
    val size: Int = 0,
    val type: String? = null
) : Parcelable

@Parcelize
data class ReviewDataModel(
    val author: String? = null,
    val authorDetails: AuthorDetailModel? = null,
    val content: String? = null,
    val createdAt: String? = null,
    val id: String? = null,
    val updatedAt: String? = null,
    val url: String? = null
) : Parcelable

@Parcelize
data class SpokenLanguageModel(
    val englishName: String? = null,
    val iso6391: String? = null,
    val name: String? = null
) : Parcelable

@Parcelize
data class AuthorDetailModel(
    val avatarPath: String? = null,
    val name: String? = null,
    val rating: Double? = 0.0,
    val username: String? = null
) : Parcelable