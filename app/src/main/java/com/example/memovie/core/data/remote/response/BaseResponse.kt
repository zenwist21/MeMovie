package com.test.movieApp.core.data.remote.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("currentPage")
    val currentPage: Int = 0,
    @SerializedName("results")
    val results: T? = null,
    @SerializedName("total_pages")
    val totalPages: Int = 0
)