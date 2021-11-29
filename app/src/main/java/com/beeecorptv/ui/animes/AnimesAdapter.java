package com.beeecorptv.ui.animes;

import android.annotation.SuppressLint;
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
import com.beeecorptv.util.Tools;
import java.util.List;

import static com.beeecorptv.util.Constants.ARG_MOVIE;

/**
 * Adapter for Animes.
 *
 * @author Yobex.
 */
public class AnimesAdapter extends RecyclerView.Adapter<AnimesAdapter.AnimeViewHolder> {

    private List<Media> animeList;
    private Context context;


    @SuppressLint("NotifyDataSetChanged")
    public void addToContent(List<Media> castList, Context context) {
        this.animeList = castList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AnimesAdapter.AnimeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (animeList != null) {
            return animeList.size();
        } else {
            return 0;
        }
    }

    class AnimeViewHolder extends RecyclerView.ViewHolder {


        private final ItemMovieBinding binding;


        AnimeViewHolder (@NonNull ItemMovieBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {


            final Media anime = animeList.get(position);

            binding.movietitle.setText(anime.getName());


            if (anime.getNewEpisodes() == 1) {

                binding.hasNewEpisodes.setVisibility(View.VISIBLE);
            }

            binding.rootLayout.setOnClickListener(v -> {

                Intent intent = new Intent(context, AnimeDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, anime);
                context.startActivity(intent);


            });


            binding.ratingBar.setRating(anime.getVoteAverage() / 2);
            binding.viewMovieRating.setText(String.valueOf(anime.getVoteAverage()));

            Tools.onLoadMediaCover(context,binding.itemMovieImage,anime.getPosterPath());


            for (Genre genre : anime.getGenres()) {
                binding.mgenres.setText(genre.getName());
            }


        }
    }
}
