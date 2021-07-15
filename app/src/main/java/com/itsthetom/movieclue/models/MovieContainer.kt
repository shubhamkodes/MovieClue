package com.itsthetom.movieclue.models

data class MovieContainer(
    public val page: Int,
    public val results: List<MovieModel>,
    public val total_results:Int,
    public val total_pages:Int

)