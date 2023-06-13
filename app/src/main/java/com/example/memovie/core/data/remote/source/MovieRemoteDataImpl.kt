package com.example.memovie.core.data.remote.source

import com.example.memovie.core.data.model.GenreModel
import com.example.memovie.core.data.model.Resource
import com.example.memovie.core.data.model.ReviewDataModel
import com.example.memovie.core.data.model.TmDbModel
import com.example.memovie.core.data.model.VideoModel
import com.example.memovie.core.data.model.convertToGenreList
import com.example.memovie.core.data.model.convertToReviewList
import com.example.memovie.core.data.model.convertToTMDbListModel
import com.example.memovie.core.data.model.convertToTmDBModel
import com.example.memovie.core.data.model.convertToVideoList
import com.example.memovie.core.data.remote.network.ApiService
import com.example.memovie.core.domain.source.MovieRemoteDataSource
import com.example.memovie.core.utils.NetworkConnectivity
import com.example.memovie.core.utils.NetworkConstant.NETWORK_ERROR
import com.example.memovie.core.utils.NetworkConstant.NOT_FOUND
import com.example.memovie.core.utils.NetworkConstant.NO_INTERNET
import com.example.memovie.core.utils.convertErrorMessage
import com.test.movieApp.core.data.remote.response.BaseResponse
import java.io.IOException
import javax.inject.Inject

class MovieRemoteDataImpl @Inject constructor(
    private val apiService: ApiService,
    private val networkConnectivity: NetworkConnectivity
) : MovieRemoteDataSource {

    override suspend fun getListMovie(params: HashMap<String, Any>): Resource<BaseResponse<MutableList<TmDbModel>>> {
        val responseCall = apiService::getMovieList
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(params)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(
                data = response.body()?.convertToTMDbListModel() ?: BaseResponse(results = mutableListOf())
            )

        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getListTrendingMovie(): Resource<BaseResponse<MutableList<TmDbModel>>> {
        val responseCall = apiService::getTrendingMovies
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke("en-US", 1)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(
                data = response.body()?.convertToTMDbListModel() ?: BaseResponse(results = mutableListOf())
            )

        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getListTrendingTvShow(): Resource<BaseResponse<MutableList<TmDbModel>>> {
        val responseCall = apiService::getTrendingTvShow
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke("en-US", 1)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(
                data = response.body()?.convertToTMDbListModel() ?: BaseResponse(results = mutableListOf())
            )

        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getSearched(params: HashMap<String, Any>): Resource<BaseResponse<MutableList<TmDbModel>>> {
        val responseCall = apiService::getSearchedMovies
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(params)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(
                data = response.body()?.convertToTMDbListModel() ?: BaseResponse(results = mutableListOf())
            )

        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getMovieGenres(language: String): Resource<MutableList<GenreModel>> {
        val responseCall = apiService::getMovieGenres
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(language)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.genres.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.genres?.convertToGenreList() ?: mutableListOf())
        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getListTvShow(params: HashMap<String, Any>): Resource<BaseResponse<MutableList<TmDbModel>>> {
        val responseCall = apiService::getTvShowList
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(params)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.convertToTMDbListModel() ?:BaseResponse(results = mutableListOf()))
        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getTVShowGenres(language: String): Resource<MutableList<GenreModel>> {
        val responseCall = apiService::getTvGenres
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(language)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.genres.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.genres?.convertToGenreList() ?: mutableListOf())
        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getDetailTVShow(tvID: Int): Resource<TmDbModel> {
        val responseCall = apiService::getDetailTv
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(tvID)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            if (response.body() == null) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.convertToTmDBModel() ?: TmDbModel())

        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getDetailMovie(movieID: Int): Resource<TmDbModel> {
        val responseCall = apiService::getDetailMovie
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(movieID)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            if (response.body() == null) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.convertToTmDBModel() ?: TmDbModel())

        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getReviewMovie(params: HashMap<String, Any>): Resource<BaseResponse<MutableList<ReviewDataModel>>> {
        val responseCall = apiService::getReviewMovie
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response =
                responseCall.invoke(params["movie_id"].toString(), params["page"].toString())
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )

            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.convertToReviewList() ?: BaseResponse(results = mutableListOf()))

        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getReviewTV(params: HashMap<String, Any>): Resource<BaseResponse<MutableList<ReviewDataModel>>> {
        val responseCall = apiService::getReviewTv
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(
                params["movie_id"].toString(),
                params["page"].toString(),
                "en-US"
            )
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )

            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.convertToReviewList() ?: BaseResponse(results = mutableListOf()))
        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getMovieVideos(movieID: Int): Resource<BaseResponse<MutableList<VideoModel>>> {
        val responseCall = apiService::getMovieVideo
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(movieID)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.convertToVideoList() ?: BaseResponse(results = mutableListOf()))
        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }

    override suspend fun getTvVideos(tvID: Int): Resource<BaseResponse<MutableList<VideoModel>>> {
        val responseCall = apiService::getTvVideo
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorMessage = NO_INTERNET)
        }
        return try {
            val response = responseCall.invoke(tvID)
            if (!response.isSuccessful) {
                return Resource.DataError(
                    errorMessage = convertErrorMessage(response.errorBody())
                )
            }
            if (response.body()?.results.isNullOrEmpty()) {
                return Resource.DataError(
                    errorMessage = NOT_FOUND
                )
            }
            Resource.Success(data = response.body()?.convertToVideoList() ?: BaseResponse(results = mutableListOf()))
        } catch (e: IOException) {
            return Resource.DataError(errorMessage = NETWORK_ERROR)
        }
    }


}