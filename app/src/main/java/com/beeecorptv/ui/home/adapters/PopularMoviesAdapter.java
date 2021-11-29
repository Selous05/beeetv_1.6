package com.beeecorptv.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.databinding.ItemMovieBinding;
import com.beeecorptv.ui.moviedetails.MovieDetailsActivity;
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.Tools;
import java.util.List;


/**
 * Adapter for Popular Movies.
 *
 * @author Yobex.
 */
public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.MainViewHolder> {

    private List<Media> castList;
    private Context context;


    public void addPopular(List<Media> castList,Context context) {
        this.castList = castList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularMoviesAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new PopularMoviesAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularMoviesAdapter.MainViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieBinding binding;

        MainViewHolder(@NonNull ItemMovieBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }



        void onBind(final int position) {


            final Media media = castList.get(position);


            if (media.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }

            binding.movietitle.setText(media.getTitle());





         if (media.getPremuim() == 1) {

           binding.moviePremuim.setVisibility(View.VISIBLE);

            }else {

          binding.moviePremuim.setVisibility(View.GONE);
        }


            binding.rootLayout.setOnClickListener(view -> {


                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(Constants.ARG_MOVIE, media);
                context.startActivity(intent);


            });

            binding.ratingBar.setRating(media.getVoteAverage() / 2);
            binding.viewMovieRating.setText(String.valueOf(media.getVoteAverage()));

            Tools.onLoadMediaCoverAdapters(context,binding.itemMovieImage, media.getPosterPath());

            for (Genre genre : media.getGenres()) {
                binding.mgenres.setText(genre.getName());
            }

        }

    }
}

