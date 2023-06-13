package com.example.memovie.presentation.review

import com.example.memovie.core.data.model.ReviewDataModel
import com.example.memovie.presentation.detail.DetailType

data class ReviewUiState(
    val initialLoading: Boolean = false,
    val loadingNextPage: Boolean = false,
    val error: String? = null,
    val listReview: List<ReviewDataModel> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val itemId: Int? = null,
    val type: DetailType? = DetailType.MOVIE
)