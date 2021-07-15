package com.itsthetom.movieclue

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.itsthetom.movieclue.databinding.ActivityDetailBinding
import com.itsthetom.movieclue.models.MovieModel
import com.itsthetom.movieclue.utils.ConnectionLiveData
import com.itsthetom.movieclue.utils.Credentials
import com.itsthetom.movieclue.utils.Genres
import com.itsthetom.movieclue.utils.TempUtil
import com.jama.carouselview.enums.IndicatorAnimationType
import com.jama.carouselview.enums.OffsetType
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailBinding
    private lateinit var viewModel:DetailViewModel
    private var detail_type="Movie"
    private var isInternetActive=true;
    companion object{
        const val MOVIE_TYPE="Movie"
        const val SERIES_TYPE="Series"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this).get(DetailViewModel::class.java)

        //TempUtil.hideSystemUI(window)

        val movie= Gson().fromJson<MovieModel>(intent.getStringExtra("movie"),MovieModel::class.java)
        detail_type= intent.getStringExtra("type").toString()


        ConnectionLiveData(this).observe(this,{
            if (it){
                binding.notNetworkLayout.visibility=View.GONE
                isInternetActive=it

            }else{
                binding.notNetworkLayout.visibility=View.VISIBLE
                isInternetActive=it
            }
        })

        if (movie!=null){
            initViews(movie)
        }
    }

    private fun initViews(movie:MovieModel) {
        Log.d("TAG","exceptione rror : 2")

        if (isInternetActive){
            setImage(
                this,
                Credentials.BASE_IMG_URL + movie.poster_path,
                binding.detailCardView,
                binding.movieImg,
                null
            )
        }

        binding.tvGenres.text=""
        movie.genres?.forEach{
            binding.tvGenres.append(it.toString())
        }

        binding.tvSimilar.visibility=View.GONE
        binding.tvTrailerVideos.visibility= View.GONE
        binding.videoSlider.visibility=View.GONE

        binding.ratingBar.rating = movie.vote_average / 2
        binding.ratingBar.isEnabled = false

        binding.tvMovieTitle.text = movie.title
        binding.tvRelease.text = "Release : ${movie!!.release_date}"
        binding.tvMovieDesc.text = movie.overview
        binding.tvImdbRating.text = movie.vote_average.toString().subSequence(0,3)
        binding.tvRuntime.text = movie.runtime.toString()


        Log.d("TAG","exceptione rror : 3")
        try{
            ConnectionLiveData(this).observe(this,{
                if (it) {
                    viewModel.fetchMovieVideos(movie.id)
                    setSimilar(movie.id)
                    setTrailerAndVideo()
                }
            })
        }catch (ex:Exception){
            Log.d("TAG","exceptione rror : ${ex.message.toString()}")
        }


    }

    private fun setSimilar(id:Int) {
        viewModel.getSimilar(detail_type,id).observe(this, Observer {

            binding.tvSimilar.visibility=View.VISIBLE
            val v: RecyclerView = binding.videoSlider.findViewById(R.id.carouselRecyclerView)
            v.onFlingListener = null
            binding.similarMovieSlider.apply {
                size = it.results.size
                resource = R.layout.card_movie
                setCarouselViewListener { view, position ->

                    val imageView = view.findViewById<ImageView>(R.id.movieImg)
                    val imdbRating = view.findViewById<TextView>(R.id.tvImdbRating)

                    imdbRating.text=it.results[position].vote_average.toString().substring(0,3)

                    val movie=it.results[position]
                    imageView.setOnClickListener{
                       // viewModel.getSimilar(detail_type,movie.id)
                        if (isInternetActive)
                            initViews(movie)
                        binding.detailScrollView.fullScroll(Gravity.TOP)
                    }
                    val shimmer: ShimmerFrameLayout =view.findViewById(R.id.shimmer)
                    setImage(context,Credentials.BASE_IMG_URL+it.results[position].poster_path,null,imageView,shimmer)
                }

                show()
                hideIndicator(true)

            }
        })
    }


    private fun setTrailerAndVideo() {
        try{
            viewModel.getVideoContainer().observe(this, Observer {
                println("debug : observed data ${it.results.size}")
                if (it.results.isNotEmpty()) {
                    binding.tvTrailerVideos.visibility = View.VISIBLE
                    binding.videoSlider.visibility = View.VISIBLE
                }else{
                    binding.tvTrailerVideos.visibility = View.GONE
                    binding.videoSlider.visibility = View.GONE
                }

                val v: RecyclerView = binding.videoSlider.findViewById(R.id.carouselRecyclerView)
                v.onFlingListener = null
                binding.videoSlider.apply {
                    size = it.results.size
                    resource = R.layout.video_card
                    indicatorAnimationType= IndicatorAnimationType.DROP
                    carouselOffset = OffsetType.CENTER

                    setCarouselViewListener { view, position ->
                        val videoView = view.findViewById<YouTubePlayerView>(R.id.youtubeVideoPlayer)
                        videoView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                               if (isInternetActive) {
                                   youTubePlayer.loadVideo(it.results[position].key, 0f)
                                   youTubePlayer.pause()
                               }
                            }
                        })
                    }
                    // After you finish setting up, show the CarouselView
                    show()
                    hideIndicator(false)
                }
            })
        }catch (ex:Exception){
            Log.d("TAG",ex.message.toString())
        }



    }

    private fun setImage(ctx: Context, url:String, cardView: CardView?, imageView: ImageView,shimmerLayout:ShimmerFrameLayout?){
        shimmerLayout?.showShimmer(true)

            if (isInternetActive){
                Glide.with(ctx)
                    .asBitmap()
                    .load(url)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {

                            if (resource != null && cardView!=null) {
                                Palette.from(resource)
                                    .generate(object : Palette.PaletteAsyncListener{
                                        override fun onGenerated(palette: Palette?) {
                                            val darkVibrantSwatch: Palette.Swatch? =
                                                palette?.getDarkVibrantSwatch()
                                            val dominantSwatch: Palette.Swatch? = palette?.getDominantSwatch()
                                            val lightVibrantSwatch: Palette.Swatch? =
                                                palette?.getLightVibrantSwatch()
                                            if (darkVibrantSwatch != null) {
                                                //cardView.setCardBackgroundColor(darkVibrantSwatch.getRgb())
                                                    binding.constraintLayout.setBackgroundColor(darkVibrantSwatch.rgb)
                                                val gradientDrawable2 = GradientDrawable(
                                                    GradientDrawable.Orientation.BOTTOM_TOP,
                                                    intArrayOf(darkVibrantSwatch.getRgb(), darkVibrantSwatch.getRgb())
                                                )
                                                //    binding.detailCardView.setBackground(gradientDrawable2)
                                            } else if (dominantSwatch != null) {
                                                cardView.setCardBackgroundColor(dominantSwatch.getRgb())
                                            } else {
                                                if (lightVibrantSwatch != null) {
                                                    cardView.setCardBackgroundColor(lightVibrantSwatch.getRgb())
                                                }
                                            }
                                        }
                                    })
                            }
                            shimmerLayout?.hideShimmer()
                            return false
                        }
                    })
                    .into (imageView)
            }


    }


}