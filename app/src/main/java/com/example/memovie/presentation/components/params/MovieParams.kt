package com.example.memovie.presentation.components.params

object MovieParams {
    fun getMovieParams(
        page: Int = 1,
        genres: Int? = null
    ): HashMap<String, Any> {
        val params: HashMap<String, Any> = HashMap()
        params["page"] = page
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