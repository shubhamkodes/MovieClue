package com.itsthetom.movieclue.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.itsthetom.movieclue.models.MovieContainer
import com.itsthetom.movieclue.models.MovieModel
import com.itsthetom.movieclue.models.VideoContainer
import com.itsthetom.movieclue.utils.Credentials
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object Repository {
    private var upcomingMovies:MutableLiveData<List<MovieModel>> = MutableLiveData()
    private var popularMovies:MutableLiveData<List<MovieModel>> = MutableLiveData()
    private var trendingMovies:MutableLiveData<List<MovieModel>> = MutableLiveData()
    private var topRatedMovies:MutableLiveData<List<MovieModel>> = MutableLiveData()
    private var popularSeries:MutableLiveData<List<MovieModel>> = MutableLiveData()

    private var apiclient:ApiClient = Retrofit.Builder()
        .baseUrl(Credentials.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiClient::class.java)

    suspend fun fetchAllMoviesFromApi(){
       val upcomingMovResponse=apiclient.getUpcomingMovie(Credentials.API_KEY)
        if (upcomingMovResponse.isSuccessful){
            val list=upcomingMovResponse.body()?.results
            upcomingMovies.postValue(list)
        }

        val popularMovRespone=apiclient.getPopularMovies(Credentials.API_KEY)
        if (popularMovRespone.isSuccessful){
            val list=popularMovRespone.body()?.results
            popularMovies.postValue(list)
        }

        val trendingMovieResponse=apiclient.getTrendingMovies(Credentials.MEDIA_TYPE,Credentials.TIME_WINDOW,Credentials.API_KEY)
        if (trendingMovieResponse.isSuccessful){
            val list=trendingMovieResponse.body()?.results
            trendingMovies.postValue(list)
        }else{
            println("Error in response of Trending Movies : "+trendingMovieResponse.errorBody())
        }

        val topRatedMovieResponse=apiclient.getTopRatedMovies(Credentials.API_KEY)
        if (topRatedMovieResponse.isSuccessful){
            val list=topRatedMovieResponse.body()?.results
            topRatedMovies.postValue(list)
        }

        val popularSeriesResponse=apiclient.getPopularSeries(Credentials.API_KEY)
        if (popularSeriesResponse.isSuccessful){
            val list=popularSeriesResponse.body()?.results
            popularSeries.postValue(list)
        }

    }

     fun getUpcomingMovies():LiveData<List<MovieModel>>{
        return upcomingMovies
    }

     fun getPopularMovies():LiveData<List<MovieModel>>{
        return popularMovies
    }

     fun getTrendingMovies():LiveData<List<MovieModel>>{
        return trendingMovies
    }

    fun getTopRatedMovies():LiveData<List<MovieModel>>{
        return topRatedMovies
    }

    fun getPopularSeries():LiveData<List<MovieModel>>{
        return popularSeries
    }

    suspend fun searchMovie(query:String,page:Int):Response<MovieContainer>{
        return apiclient.searchMovies(Credentials.API_KEY,query,page)
    }
    suspend fun searchSeries(query:String,page:Int):Response<MovieContainer>{
        return apiclient.searchSeries(Credentials.API_KEY,query,page)
    }

    suspend fun searchCollection(query:String,page:Int):Response<MovieContainer>{
        return apiclient.searchCollection(Credentials.API_KEY,query,page)
    }

    suspend fun getMovieVideos(id:Int):Response<VideoContainer>{
        return apiclient.getMovieVideos(id,Credentials.API_KEY)
    }

    suspend fun getSimilar(type:String,id:Int):Response<MovieContainer>{
        return apiclient.getSimilar(id,Credentials.API_KEY)
    }






}