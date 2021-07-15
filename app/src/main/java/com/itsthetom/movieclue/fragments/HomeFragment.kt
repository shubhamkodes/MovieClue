package com.itsthetom.movieclue.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.itsthetom.movieclue.DetailActivity
import com.itsthetom.movieclue.MainViewModel
import com.itsthetom.movieclue.R
import com.itsthetom.movieclue.adapters.SearchResultAdapter
import com.itsthetom.movieclue.adapters.SliderAdapter
import com.itsthetom.movieclue.databinding.FragmentHomeBinding
import com.itsthetom.movieclue.interfaces.SearchAdapterListener
import com.itsthetom.movieclue.models.MovieModel
import com.itsthetom.movieclue.utils.ConnectionLiveData
import com.itsthetom.movieclue.utils.Credentials
import com.itsthetom.movieclue.utils.TempUtil
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview
import com.jama.carouselview.CarouselView
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import java.lang.Exception


class HomeFragment : Fragment() ,SearchAdapterListener{
    private lateinit var binding:FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private var isInternetActive=true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(inflater)
        viewModel=ViewModelProvider(requireActivity()).get(MainViewModel::class.java)


        ConnectionLiveData(requireContext()).observe(viewLifecycleOwner,{
              isInternetActive=it
        })

        try {
            setTrendingMovies()
            setUpPopularMovies()
            setUpcomingMovies()
            setUpPopularSeries();
            setUpTopRatedMovies()
        }catch (ex:Exception){
            Log.d("tag","In home fragment : "+ex.message.toString())
        }


        TempUtil.showSystemUI(requireActivity().window,binding.root)

        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment);
        }

        return binding.root
    }

    private fun setTrendingMovies() {
        viewModel.getTrendingMovies().observe(viewLifecycleOwner, Observer {
                 setTrendingResults(binding.trendingMoviesSlider,it,true)
              }
        )
    }

       private fun setUpPopularMovies() {

        viewModel.getPopularMovies().observe(viewLifecycleOwner, Observer {
            setDataIntoUi(
                binding.popularMovieRecyclerView,
                it,
                R.layout.card_movie,
                DetailActivity.MOVIE_TYPE,
                0
            )
        })

    }

    private fun setUpcomingMovies() {

        val sliderAdapter=SliderAdapter(context);
        binding.imageSlider.setSliderAdapter(sliderAdapter)

        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.startAutoCycle();
        viewModel.getUpcomingMovies().observe(viewLifecycleOwner, Observer {
            sliderAdapter.addList(it)
        })

    }


    private fun setUpPopularSeries() {
        viewModel.getPopularSeries().observe(viewLifecycleOwner, Observer {
            setDataIntoUi(
                binding.popSeriesSlider,
                it,
                R.layout.card_movie,
                DetailActivity.SERIES_TYPE,
                0
            )
        })
    }

    private fun setUpTopRatedMovies() {
        viewModel.getTopRatedMovies().observe(viewLifecycleOwner, {
            setDataIntoUi(
                binding.topRatedMovieSlider,
                it,
                R.layout.card_movie,
                DetailActivity.MOVIE_TYPE,
                0
            )

        })
    }

    private fun setDataIntoUi(
        slider: CarouselView,
        it: List<MovieModel>?,
        cardView: Int,
        type: String,
        currentItemPos:Int
    ) {
        try {
            if (it != null) {
                slider.apply {
                    size = it.size
                    resource = cardView
                    setCarouselViewListener { view, position ->

                        val imageView = view.findViewById<ImageView>(R.id.movieImg)
                        val imdbRating = view.findViewById<TextView>(R.id.tvImdbRating)

                        imdbRating.text=it[position].vote_average.toString()

                        // Navigate to movie detail fragment to show the full details of movie/series
                        val movie=it[position]
                        imageView.setOnClickListener{
                            startMovieDetail(movie,type)
                        }

                        val shimmer:ShimmerFrameLayout=view.findViewById(R.id.shimmer)
                        setImg(context,Credentials.BASE_IMG_URL+it[position].poster_path,imageView,shimmer)
                    }

                    show()
                    hideIndicator(true)

                }

            }
        }catch (ex:Exception){
            Log.d("TAG","fun setDataIntoUi : "+ex.message.toString())
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setTrendingResults(trendingRv:CarouselRecyclerview,seriesList: List<MovieModel>,wideLayout:Boolean){
        Log.d("TAG","treending total results are ${seriesList.size}")
        if (seriesList.isNotEmpty()){

            trendingRv.visibility=View.VISIBLE
            val movieAdapter= SearchResultAdapter(wideLayout,this)
            movieAdapter.addList(seriesList)
            trendingRv.layoutManager= trendingRv.getCarouselLayoutManager()
            trendingRv.adapter=movieAdapter

            trendingRv.set3DItem(true)
            trendingRv.setInfinite(true)
            trendingRv.setAlpha(true)

        }
    }



    private fun startMovieDetail(movie: MovieModel,type: String) {

        val gson=Gson()
        val intent= Intent(context,DetailActivity::class.java)
        intent.putExtra("movie",gson.toJson(movie))
        intent.putExtra("type",type)
        requireActivity().startActivity(intent)

    }

    private fun setImg(ctx: Context, url:String, imgView:ImageView,shimmer:ShimmerFrameLayout)  {
        shimmer.showShimmer(true)
            if (isInternetActive){
                Glide.with(ctx)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.ic_loading)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            shimmer.hideShimmer()
                            return false
                        }

                    } )
                    .into(imgView);
            }

    }

    override fun startDetailsFragment(data: MovieModel) {
        val gson=Gson()
        val intent= Intent(context,DetailActivity::class.java)
        intent.putExtra("movie",gson.toJson(data))
        intent.putExtra("type",DetailActivity.MOVIE_TYPE)
        requireActivity().startActivity(intent)
    }


}