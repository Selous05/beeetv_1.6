package com.beeecorptv.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.databinding.ItemMovieBinding;
import com.beeecorptv.ui.moviedetails.MovieDetailsActivity;
import com.beeecorptv.ui.seriedetails.SerieDetailsActivity;
import com.beeecorptv.util.Tools;

import java.util.List;

import static com.beeecorptv.util.Constants.ARG_MOVIE;

/**
 * Adapter for Movie.
 *
 * @author Yobex.
 */
public class TopTeenAdapter extends RecyclerView.Adapter<TopTeenAdapter.MainViewHolder> {

    private List<Media> castList;
    private Context context;

    public void addMain(List<Media> mediaList,Context context) {
        this.castList = mediaList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopTeenAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new TopTeenAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TopTeenAdapter.MainViewHolder holder, int position) {
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


            if (media.getTitle() !=null) {

                binding.movietitle.setText(media.getTitle());

                binding.mgenres.setText("TOP "+ (position + 1));

                binding.rootLayout.setOnLongClickListener(v -> {
                    Toast.makeText(context, ""+media.getTitle(), Toast.LENGTH_SHORT).show();
                    return false;
                });

                binding.rootLayout.setOnClickListener(view -> {


                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                });

            }else {

                binding.movietitle.setText(media.getName());
                binding.mgenres.setText("TOP "+ (position + 1));

                binding.rootLayout.setOnLongClickListener(v -> {
                    Toast.makeText(context, ""+media.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                });

                binding.rootLayout.setOnClickListener(view -> {


                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                });

            }


            if (media.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }


            binding.ratingBar.setRating(media.getVoteAverage() / 2);
            binding.viewMovieRating.setText(String.valueOf(media.getVoteAverage()));
            Tools.onLoadMediaCoverAdapters(context,binding.itemMovieImage, media.getPosterPath());

        }

    }

}
