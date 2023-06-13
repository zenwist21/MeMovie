package com.example.memovie.core.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TmDBResponse(
    @SerializedName("adult")
    val adult: Boolean = false,

    @SerializedName("backdrop_path")
    val backdropPath: String? = null,

    @SerializedName("budget")
    val budget: Int? = 0,

    @SerializedName("genres")
    val genres: List<GenreResponse>? = emptyList(),

    @SerializedName("homepage")
    val homepage: String? = null,

    @SerializedName("id")
    val id: Int? = 0,

    @SerializedName("imdb_id")
    val imdbId: String? = null,

    @SerializedName("original_language")
    val originalLanguage: String? = null,

    @SerializedName("first_air_date")
    val firstAirDate: String? = null,

    @SerializedName("original_title")
    val originalTitle: String? = null,

    @SerializedName("overview")
    val overview: String? = null,

    @SerializedName("popularity")
    val popularity: Double? = 0.0,

    @SerializedName("poster_path")
    val posterPath: String? = null,

    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompanyResponse>? = emptyList(),

    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountryResponse>? = emptyList(),

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("revenue")
    val revenue: Long? = 0,

    @SerializedName("runtime")
    val runtime: Int? = 0,

    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguageResponse>? = emptyList(),

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("tagline")
    val tagline: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("original_name")
    val originalName: String? = null,

    @SerializedName("video")
    val video: Boolean = false,

    @SerializedName("vote_average")
    val voteAverage: Double? = 0.0,

    @SerializedName("vote_count")
    val voteCount: Int? = 0,

    @SerializedName("message")
    val message: String? = "",

    @SerializedName("total_page")
    val totalPage: Int = 1,

    ) : Parcelable

@Parcelize
data class ProductionCompanyResponse(
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("logo_path")
    val logoPath: String? = null,
    @SerializedName("")
    val name: String? = null,
    @SerializedName("origin_country")
    val originCountry: String? = null
) : Parcelable

@Parcelize
data class ProductionCountryResponse(
    @SerializedName("iso_3166_1")
    val iso31661: String? = null,
    @SerializedName("name")
    val name: String? = null
) : Parcelable

@Parcelize
data class GenreResponse(
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("message")
    val message: String? = null,
    var isSelected: Boolean = false,
) : Parcelable

@Parcelize
data class VideoResponse(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("iso_3166_1")
    val iso3166: String? = null,

    @SerializedName("iso_639_1")
    val iso6391: String? = null,

    @SerializedName("key")
    val key: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("official")
    val official: Boolean = false,

    @SerializedName("published_at")
    val publishedAt: String? = null,

    @SerializedName("site")
    val site: String? = null,

    @SerializedName("size")
    val size: Int = 0,

    @SerializedName("type")
    val type: String? = null

) : Parcelable

@Parcelize
data class ReviewResponse(
    @SerializedName("author")
    val author: String? = null,
     @SerializedName("author_details")
    val authorDetails: AuthorDetailsResponse? = null,
     @SerializedName("content")
    val content: String? = null,
     @SerializedName("created_at")
    val createdAt: String? = null,
     @SerializedName("id")
    val id: String? = null,
     @SerializedName("updated_at")
    val updatedAt: String? = null,
     @SerializedName("url")
    val url: String? = null
) : Parcelable

@Parcelize
data class SpokenLanguageResponse(
    @SerializedName("english_name")
    val englishName: String? = null,
     @SerializedName("iso_639_1")
    val iso6391: String? = null,
     @SerializedName("name")
    val name: String? = null
) : Parcelable

@Parcelize
data class AuthorDetailsResponse(
    @SerializedName("avatar_path")
    val avatarPath: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("rating")
    val rating: Double? = 0.0,
    @SerializedName("username")
    val username: String? = null
) : Parcelable