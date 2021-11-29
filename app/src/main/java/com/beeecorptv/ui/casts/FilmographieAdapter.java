package com.beeecorptv.ui.casts;

import static com.beeecorptv.util.Constants.ARG_MOVIE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.databinding.ItemFilmographieBinding;
import com.beeecorptv.ui.animes.AnimeDetailsActivity;
import com.beeecorptv.ui.moviedetails.MovieDetailsActivity;
import com.beeecorptv.ui.seriedetails.SerieDetailsActivity;
import com.beeecorptv.util.Tools;

import org.jetbrains.annotations.NotNull;


/**
 * Adapter for Next Movie.
 *
 * @author Yobex.
 */
public class FilmographieAdapter extends PagedListAdapter<Media, FilmographieAdapter.NextPlayMoviesViewHolder> {

        final Context context;

    public FilmographieAdapter(Context context) {
        super(ITEM_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public NextPlayMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemFilmographieBinding binding = ItemFilmographieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new FilmographieAdapter.NextPlayMoviesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NextPlayMoviesViewHolder holder, int position) {

        Media media = getItem(position);

        holder.onBind(media);
    }


    class NextPlayMoviesViewHolder extends RecyclerView.ViewHolder {


        private final ItemFilmographieBinding binding;

        NextPlayMoviesViewHolder(@NonNull ItemFilmographieBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final Media media) {

            binding.movieName.setText(media.getName());

            binding.rootLayout.setOnClickListener(view -> {

                if (media.getType().equals("serie")) {

                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                }else  if (media.getType().equals("anime")) {


                    Intent intent = new Intent(context, AnimeDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);

                }else {

                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);
                }



            });

            Tools.onLoadMediaCoverAdapters(context,binding.imageMovie, media.getPosterPath());

        }


    }


    private static final DiffUtil.ItemCallback<Media> ITEM_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(Media oldItem, Media newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Media oldItem, @NotNull Media newItem) {
                    return oldItem.equals(newItem);
                }
            };


}
