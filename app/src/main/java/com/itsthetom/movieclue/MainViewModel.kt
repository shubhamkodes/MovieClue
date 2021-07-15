package com.itsthetom.movieclue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsthetom.movieclue.models.MovieContainer
import com.itsthetom.movieclue.models.MovieModel

import com.itsthetom.movieclue.repo.Repository
import com.itsthetom.movieclue.utils.ConnectionLiveData
import kotlinx.coroutines.launch


class MainViewModel: ViewModel(){


    private val searchedMoviesResult:MutableLiveData<MovieContainer> = MutableLiveData()
    private val searchedSeriesResult:MutableLiveData<MovieContainer> = MutableLiveData()
    private val searchedCollectionResult:MutableLiveData<MovieContainer> = MutableLiveData()


    fun fetchAllMoviesFromApi(){
        viewModelScope.launch {
            Repository.fetchAllMoviesFromApi()
        }
    }


    fun getUpcomingMovies():LiveData<List<MovieModel>>{
        return Repository.getUpcomingMovies()
    }

    fun getPopularMovies():LiveData<List<MovieModel>>{
        return Repository.getPopularMovies()
    }

    fun getTrendingMovies():LiveData<List<MovieModel>>{
        return Repository.getTrendingMovies()
    }
    fun getTopRatedMovies():LiveData<List<MovieModel>>{
        return Repository.getTopRatedMovies()
    }

    fun getPopularSeries():LiveData<List<MovieModel>>{
        return Repository.getPopularSeries()
    }

    fun searchMovie(query:String){
        viewModelScope.launch {
           val movieResponse= Repository.searchMovie(query,1)
            if (movieResponse.isSuccessful){
                searchedMoviesResult.postValue(movieResponse.body())
            }
        }
    }
    fun searchSeries(query:String){
        viewModelScope.launch {
            val seriesResponse= Repository.searchSeries(query,1)
            if (seriesResponse.isSuccessful){
                searchedSeriesResult.postValue(seriesResponse.body())
            }
        }
    }

    fun searchCollection(query:String){
        viewModelScope.launch {
            val collectionResponse= Repository.searchCollection(query,1)
            if (collectionResponse.isSuccessful){
                searchedCollectionResult.postValue(collectionResponse.body())
            }
        }
    }

    fun getSearchMovieResults():LiveData<MovieContainer>{
        return searchedMoviesResult
    }

    fun getSearchSeriesResults():LiveData<MovieContainer>{
        return searchedSeriesResult
    }

    fun getSearchCollectionResults():LiveData<MovieContainer>{
        return searchedCollectionResult
    }


    fun clearSearchedData(){
        searchedMoviesResult.value=null
        searchedSeriesResult.value=null
        searchedCollectionResult.value=null
    }







}