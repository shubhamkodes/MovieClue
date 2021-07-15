package com.itsthetom.movieclue.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.itsthetom.movieclue.R
import com.itsthetom.movieclue.databinding.CardMovieBinding
import com.itsthetom.movieclue.databinding.CardMovieTopBinding
import com.itsthetom.movieclue.interfaces.SearchAdapterListener
import com.itsthetom.movieclue.models.MovieModel
import com.itsthetom.movieclue.utils.Credentials

class SearchResultAdapter(private val isForCollection:Boolean,val listener:SearchAdapterListener):RecyclerView.Adapter<RecyclerView.ViewHolder> (){

    private var list:MutableList<MovieModel> = mutableListOf()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         val movie=list[position]
         val cardHolder:RecyclerView.ViewHolder

         // Showing Collection  of Movies in wide layout ( Card_Movie_Top )
            if (isForCollection) {
                cardHolder = holder as CollectionViewHolder
                cardHolder.binding.tvImdbRating.visibility = View.GONE
                cardHolder.binding.shimmer.showShimmer(true)
                setImage(cardHolder.binding.root.context,Credentials.BASE_IMG_URL+movie.poster_path,
                            cardHolder.binding.shimmer,cardHolder.binding.movieImg)
                cardHolder.binding.movieImg.setOnClickListener{
                    listener.startDetailsFragment(movie)
                }
                 
            }

            // Showing Movies and Series in Card_Movie Layout
            else{
                 cardHolder=holder as MovieViewHolder
                 cardHolder.binding.tvImdbRating.text=movie.vote_average.toString()
                 cardHolder.binding.shimmer.showShimmer(true)
                 setImage(cardHolder.binding.root.context,Credentials.BASE_IMG_URL+movie.poster_path,
                        cardHolder.binding.shimmer,cardHolder.binding.movieImg)

                //Start details fragment to show about details of movie/series
                 cardHolder.binding.movieImg.setOnClickListener{
                     listener.startDetailsFragment(movie)
                 }
            }

    }

    private fun setImage(context: Context,url:String,shimmer:ShimmerFrameLayout,imgView:ImageView){
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_loading)
            .listener(object : RequestListener<Drawable>{
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(isForCollection) {
           val view = LayoutInflater.from(parent.context).inflate(R.layout.card_movie_top, parent, false);
            return CollectionViewHolder(view)
        }

        val view=LayoutInflater.from(parent.context).inflate(R.layout.card_movie,parent,false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
            return list.size
    }

    fun addList(list:List<MovieModel>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }


    inner class MovieViewHolder(val view: View):RecyclerView.ViewHolder(view){
                 val binding:CardMovieBinding= CardMovieBinding.bind(view)
    }

    inner class CollectionViewHolder(val view: View):RecyclerView.ViewHolder(view){
                 val binding:CardMovieTopBinding= CardMovieTopBinding.bind(view)
    }
}