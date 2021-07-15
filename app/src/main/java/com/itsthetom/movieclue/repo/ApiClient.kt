package com.itsthetom.movieclue.repo

import com.itsthetom.movieclue.models.MovieContainer
import com.itsthetom.movieclue.models.VideoContainer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {

    @GET("3/movie/upcoming")
    suspend fun getUpcomingMovie(
        @Query("api_key") api_key:String
    ): Response<MovieContainer>

    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") api_key: String
    ):Response<MovieContainer>

//    Get a list of movies in theatres.
    @GET("3/trending/{media_type}/{time_window}")
    suspend fun getTrendingMovies(

        @Path("media_type") media_type:String,
        @Path("time_window") time_window:String,
         @Query("api_key") api_key: String
    ):Response<MovieContainer>

    @GET("3/movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") api_key: String
    ):Response<MovieContainer>

    @GET("3/tv/popular")
    suspend fun getPopularSeries(
        @Query("api_key") api_key: String
    ):Response<MovieContainer>


    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("api_key") api_key:String,
        @Query("query") query:String,
        @Query("page") page:Int
    ):Response<MovieContainer>

    @GET("3/search/tv")
    suspend fun searchSeries(
        @Query("api_key") api_key:String,
        @Query("query") query:String,
        @Query("page") page:Int
    ):Response<MovieContainer>

    @GET("3/search/collection")
    suspend fun searchCollection(
        @Query("api_key") api_key:String,
        @Query("query") query:String,
        @Query("page") page:Int
    ):Response<MovieContainer>

    @GET("/3/movie/{id}/videos")
    suspend fun getMovieVideos(
        @Path("id") id:Int,
        @Query("api_key") api_key:String
    ):Response<VideoContainer>

    @GET("/3/movie/{movie_id}/similar")
    suspend fun getSimilar(
        @Path("movie_id") movie_id:Int,
        @Query("api_key") api_key:String
    ):Response<MovieContainer>
}