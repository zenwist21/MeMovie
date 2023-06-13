package com.example.memovie.core.data.model

import com.example.memovie.core.data.remote.response.AuthorDetailsResponse
import com.example.memovie.core.data.remote.response.GenreResponse
import com.example.memovie.core.data.remote.response.ProductionCompanyResponse
import com.example.memovie.core.data.remote.response.ProductionCountryResponse
import com.example.memovie.core.data.remote.response.ReviewResponse
import com.example.memovie.core.data.remote.response.SpokenLanguageResponse
import com.example.memovie.core.data.remote.response.TmDBResponse
import com.example.memovie.core.data.remote.response.VideoResponse
import com.test.movieApp.core.data.remote.response.BaseResponse

fun ProductionCompanyResponse.convertToModel() =
    ProductionCompanyModel(
        id,
        logoPath,
        name,
        originCountry,
    )

fun List<ProductionCompanyResponse>.convertToList(): MutableList<ProductionCompanyModel> {
    val data = mutableListOf<ProductionCompanyModel>()
    if (isNotEmpty()) {
        forEach { dForm ->
            data.add(dForm.convertToModel())
        }
    }
    return data
}

fun ProductionCountryResponse.convertToCountryModel() =
    ProductionCountryModel(
        iso31661,
        name,
    )

fun List<ProductionCountryResponse>.convertToListCountry(): MutableList<ProductionCountryModel> {
    val data = mutableListOf<ProductionCountryModel>()
    if (isNotEmpty()) {
        forEach { dForm ->
            data.add(dForm.convertToCountryModel())
        }
    }
    return data
}

private fun GenreResponse.convertToGenreModel() =
    GenreModel(
        id,
        name,
        message,
        isSelected,
    )

fun List<GenreResponse>.convertToGenreList(): MutableList<GenreModel> {
    val data = mutableListOf<GenreModel>()
    if (isNotEmpty()) {
        forEach { dForm ->
            data.add(dForm.convertToGenreModel())
        }
    }
    return data
}

private fun VideoResponse.convertToVideoModel() =
    VideoModel(
        id,
        iso3166,
        iso6391,
        key,
        name,
        official,
        publishedAt,
        site,
        size,
        type
    )

fun BaseResponse<MutableList<VideoResponse>>.convertToVideoList(): BaseResponse<MutableList<VideoModel>> {
    val data = mutableListOf<VideoModel>()
    if (!results.isNullOrEmpty()) {
        results.forEach { dForm ->
            data.add(dForm.convertToVideoModel())
        }
    }
    return BaseResponse(
        currentPage,
        results = data,
        totalPages
    )
}


fun TmDBResponse.convertToTmDBModel(): TmDbModel {
    return TmDbModel(
        adult,
        backdropPath,
        budget,
        genres?.convertToGenreList() ?: emptyList(),
        homepage,
        id,
        imdbId,
        originalLanguage,
        firstAirDate,
        originalTitle,
        overview,
        popularity,
        posterPath,
        productionCompanies?.convertToList() ?: emptyList(),
        productionCountries?.convertToListCountry() ?: emptyList(),
        releaseDate,
        revenue,
        runtime,
        spokenLanguages?.convertToSpokenList() ?: emptyList() ,
        status,
        tagline,
        title,
        name,
        originalName,
        video,
        voteAverage,
        voteCount,
        message,
        totalPage,
    )
}

fun BaseResponse<MutableList<TmDBResponse>>.convertToTMDbListModel(): BaseResponse<MutableList<TmDbModel>> {
    val data = mutableListOf<TmDbModel>()
    if (!results.isNullOrEmpty()) {
        results.forEach { dForm ->
            data.add(dForm.convertToTmDBModel())
        }
    }
    return BaseResponse(
        currentPage,
        results = data,
        totalPages
    )
}
private fun ReviewResponse.convertToReviewModel() =
    ReviewDataModel(
        author,
        authorDetails?.convertAuthorDetailModel() ?: AuthorDetailModel(),
        content,
        createdAt,
        id,
        updatedAt,
        url,
    )

fun BaseResponse<MutableList<ReviewResponse>>.convertToReviewList(): BaseResponse<MutableList<ReviewDataModel>> {
    val data = mutableListOf<ReviewDataModel>()
    if (!results.isNullOrEmpty()) {
        results.forEach { dForm ->
            data.add(dForm.convertToReviewModel())
        }
    }
    return BaseResponse(
        currentPage,
        results = data,
        totalPages
    )
}

private fun SpokenLanguageResponse.convertToSpokenModel() =
    SpokenLanguageModel(
        englishName,
        iso6391,
        name,
    )

fun List<SpokenLanguageResponse>.convertToSpokenList(): MutableList<SpokenLanguageModel> {
    val data = mutableListOf<SpokenLanguageModel>()
    if (isNotEmpty()) {
        forEach { dForm ->
            data.add(dForm.convertToSpokenModel())
        }
    }
    return data
}

fun AuthorDetailsResponse.convertAuthorDetailModel() =
    AuthorDetailModel(
        avatarPath,
        name,
        rating,
        username,
    )
