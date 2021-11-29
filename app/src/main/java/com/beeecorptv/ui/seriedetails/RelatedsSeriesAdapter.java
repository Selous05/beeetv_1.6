package com.beeecorptv.ui.seriedetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.databinding.ItemRelatedsBinding;
import com.beeecorptv.ui.animes.AnimeDetailsActivity;
import com.beeecorptv.util.Tools;

import java.util.List;

import static com.beeecorptv.util.Constants.ARG_MOVIE;

/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class RelatedsSeriesAdapter extends RecyclerView.Adapter<RelatedsSeriesAdapter.MainViewHolder> {

    private List<Media> castList;

    public void addToContent(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RelatedsSeriesAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemRelatedsBinding binding = ItemRelatedsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new RelatedsSeriesAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedsSeriesAdapter.MainViewHolder holder, int position) {
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



        private final ItemRelatedsBinding binding;


        MainViewHolder(@NonNull ItemRelatedsBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }


        void onBind(final int position) {


            final Media related = castList.get(position);
            Context context = binding.imageMovie.getContext();

            binding.movieName.setText(related.getName());


            binding.rootLayout.setOnClickListener(v -> {

                if (related.getIsAnime() == 1) {

                    ((Activity)context).finish();
                    Intent intent = new Intent(context, AnimeDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, related);
                    context.startActivity(intent);

                }else {

                    ((Activity)context).finish();
                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, related);
                    context.startActivity(intent);

                }

            });

            Tools.onLoadMediaCover(context,binding.imageMovie,related.getPosterPath());


        }
    }
}
