package com.example.memovie.presentation.components.params

object MovieParams {
    fun getMovieParams(
        page: Int = 1,
        genres: List<Int>? = null
    ): HashMap<String, Any> {
        val params: HashMap<String, Any> = HashMap()
        params["page"] = page
        if (genres != null) params["with_genres"] = genres

        return params
    }

    fun getSearchParam(
        query: String,
        genres: List<Int>? = null
    ): HashMap<String, Any> {
        val params: HashMap<String, Any> = HashMap()
        params["query"] = query
        params["page"] = 1
        params["include_adult"] = false
        params["language"] = "en-US"
        if (genres != null) params["with_genres"] = genres
        return params
    }

    fun getReview(
        page: Int = 1,
        movieId: Int? = null
    ): HashMap<String, Any> {
        val params: HashMap<String, Any> = HashMap()
        params["page"] = page
        if (movieId != null) params["movie_id"] = movieId
        return params
    }
}