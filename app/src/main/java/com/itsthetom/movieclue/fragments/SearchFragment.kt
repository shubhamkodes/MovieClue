package com.itsthetom.movieclue.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.itsthetom.movieclue.DetailActivity
import com.itsthetom.movieclue.MainViewModel
import com.itsthetom.movieclue.adapters.SearchResultAdapter
import com.itsthetom.movieclue.databinding.FragmentSearchBinding
import com.itsthetom.movieclue.interfaces.SearchAdapterListener
import com.itsthetom.movieclue.models.MovieContainer
import com.itsthetom.movieclue.models.MovieModel
import com.itsthetom.movieclue.utils.ConnectionLiveData
import com.itsthetom.movieclue.utils.TempUtil
import kotlinx.coroutines.*



class SearchFragment : Fragment() ,SearchView.OnQueryTextListener,SearchAdapterListener{
    private lateinit var binding:FragmentSearchBinding
    private lateinit var viewModel:MainViewModel
    private var movieScope:Job?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSearchBinding.inflate(inflater)
        viewModel= ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        movieScope=null

        initView()

        //Observing search results
        observeSearchResults()

        //Hide status bar to show full screen
        TempUtil.showSystemUI(requireActivity().window,binding.root)

        requireActivity().showKeyboard(binding.searchBar)

        return binding.root
    }

    private fun initView() {
        binding.btnBack.setOnClickListener {
            viewModel.clearSearchedData()
            findNavController().navigateUp()

        }

        binding.searchBar.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String): Boolean {

//      If another searching is going on, then first cancelling it here to make new one
        if (movieScope!=null && movieScope!!.isActive ){
            movieScope!!.cancel()
        }

        if (query.trim() != "")
        {
        //Fetch movie, series and collections of the query user typed
            ConnectionLiveData(requireContext()).observe(this,{ isInternet ->
                if (isInternet){
                    binding.searchProgressBar.visibility=View.VISIBLE
                    viewModel.searchMovie(query)
                    viewModel.searchSeries(query)
                    viewModel.searchCollection(query)
                }
                else{
                    Log.d("TAG","Internet is off")
                }
            }
            )

        }
        return false
    }

    private fun observeSearchResults(){
        val noOfResults=0;
        //Observing data, if user make search, then it update it'll fetch it and set it into UI
        viewModel.getSearchMovieResults().observe(viewLifecycleOwner, {
            it?.let {
                if(it.total_results>0)
                     setSearchedMovieResults(it)
                else{

                 }
            }
         })


        viewModel.getSearchSeriesResults().observe(viewLifecycleOwner, {
            if (it!=null) {
                setSearchedSeriesResults(it)
            }
        })


          viewModel.getSearchCollectionResults().observe(viewLifecycleOwner, {
              if(it!=null) {
                  setSearchedCollectionResults(it)
              }
            })
    }

    // Showing Movies in UI
    private fun setSearchedMovieResults(movieList:MovieContainer){
        if (movieList.total_results!=0){
            binding.searchProgressBar.visibility=View.INVISIBLE
            setSearchedResults("Movies" ,binding.tvMovies,binding.searchedMoviesRv,movieList)
         }
    }

    // Showing Series in UI
    private fun setSearchedSeriesResults(seriesList:MovieContainer){
        Log.d("TAG","total results are ${seriesList.total_results}")
        if (seriesList.total_results!=0){
            binding.searchProgressBar.visibility=View.INVISIBLE
            setSearchedResults("Series",binding.tvSeries,binding.seriesRv,seriesList)
        }
    }

    //This method set data in UI for movies and series
    @SuppressLint("SetTextI18n")
    private fun setSearchedResults(title:String, titleType: TextView, searchedRv: RecyclerView, list: MovieContainer) {
        val it=list.results
        print("results ${list.total_results}")
        //  visibleMovieLayout()
        binding.imgSearchMan.visibility=View.GONE

        titleType.text="$title (${list.total_results})"
        titleType.visibility=View.VISIBLE
        searchedRv.visibility=View.VISIBLE

        val movieAdapter=SearchResultAdapter(false,this)
        movieAdapter.addList(it)
        searchedRv.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        searchedRv.adapter=movieAdapter
    }

    // Showing Collections of Movies in UI
    @SuppressLint("SetTextI18n")
    private fun setSearchedCollectionResults(seriesList:MovieContainer){
        Log.d("TAG","total results are ${seriesList.total_results}")
        if (seriesList.total_results!=0){
            binding.searchProgressBar.visibility=View.INVISIBLE
            val it=seriesList.results
            print("results ${seriesList.total_results}")
            //  visibleMovieLayout()
            binding.imgSearchMan.visibility=View.GONE

            binding.tvCollection.text="Collections (${seriesList.total_results})"
            binding.tvCollection.visibility=View.VISIBLE
            binding.collectionRv.visibility=View.VISIBLE

            val movieAdapter=SearchResultAdapter(true,this)
            movieAdapter.addList(it)
            binding.collectionRv.layoutManager= binding.collectionRv.getCarouselLayoutManager()
            binding.collectionRv.adapter=movieAdapter

            binding.collectionRv.set3DItem(true)
            binding.collectionRv.setInfinite(false)
            binding.collectionRv.setAlpha(true)

        }
    }


    override fun onQueryTextChange(newText: String?): Boolean {
            if (newText?.trim().equals("")){
                viewModel.clearSearchedData()

                binding.searchProgressBar.visibility=View.INVISIBLE
                binding.imgSearchMan.visibility=View.VISIBLE

                binding.searchedMoviesRv.adapter=null
                binding.seriesRv.adapter=null
                binding.collectionRv.adapter=null

                binding.searchedMoviesRv.visibility=View.GONE
                binding.seriesRv.visibility=View.GONE
                binding.collectionRv.visibility=View.GONE

                binding.tvMovies.visibility=View.GONE
                binding.tvCollection.visibility=View.GONE
                binding.tvSeries.visibility=View.GONE
            }

        return false
    }


    //Showing keyboard just after activity opened
    fun Context.showKeyboard(editText: SearchView) {

            editText.performClick()
            editText.requestFocus()

            viewModel.viewModelScope.launch {
                launch {
                    delay(200L)
                    val inputMethodManager: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInputFromWindow(
                        editText.applicationWindowToken,
                        InputMethodManager.SHOW_IMPLICIT, 0
                    )
                }
            }

    }

    override fun startDetailsFragment(data: MovieModel) {
        val gson= Gson()
        val intent= Intent(context,DetailActivity::class.java)
        intent.putExtra("movie",gson.toJson(data))
        requireActivity().startActivity(intent)
    }


}