package com.itsthetom.movieclue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.itsthetom.movieclue.R;
import com.itsthetom.movieclue.databinding.CardMovieWideBinding;
import com.itsthetom.movieclue.models.MovieModel;
import com.itsthetom.movieclue.utils.Credentials;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<MovieModel> mSliderItems = new ArrayList<>();

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<MovieModel> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(MovieModel sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    public void addList(List<MovieModel> list)
    {
        this.mSliderItems=list;
        notifyDataSetChanged();
    }
    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_movie_wide, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        MovieModel sliderItem = mSliderItems.get(position);


        Glide.with(viewHolder.binding.getRoot())
                .load(Credentials.BASE_IMG_URL +sliderItem.getBackdrop_path())
                .fitCenter()
                .into(viewHolder.binding.movieImgWide);
        viewHolder.binding.tvMovieTitleWide.setText(sliderItem.getTitle() );
        viewHolder.binding.tvReleaseDateWide.setText("( "+sliderItem.getRelease_date()+" )");

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

         CardMovieWideBinding binding;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            binding=CardMovieWideBinding.bind(itemView);
                     }
    }

}
