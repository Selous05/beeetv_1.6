package com.beeecorptv.ui.streaming;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.databinding.ItemShowStreamingBinding;
import com.beeecorptv.util.GlideApp;
import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.util.Constants.ARG_MOVIE;

/**
 * Adapter for  Streaming Channels
 *
 * @author Yobex.
 */
public class LatestStreamingAdapter extends RecyclerView.Adapter<LatestStreamingAdapter.StreamingViewHolder> {

    private List<Media> streamingList;
    private Context context;


    public void addStreaming(Context context,List<Media> castList) {
        this.streamingList = castList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StreamingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemShowStreamingBinding binding = ItemShowStreamingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new LatestStreamingAdapter.StreamingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamingViewHolder holder, int position) {
        holder.onBind(position);

    }

    @Override
    public int getItemCount() {
        if (streamingList != null) {
            return streamingList.size();
        } else {
            return 0;
        }
    }

    class StreamingViewHolder extends RecyclerView.ViewHolder {

        private final ItemShowStreamingBinding binding;


        StreamingViewHolder(@NonNull ItemShowStreamingBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = streamingList.get(position);

            for (Genre genre : media.getGenres()) {
                binding.mgenres.setText(genre.getName());
            }

            GlideApp.with(context).asBitmap().load(media.getPosterPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .placeholder(R.color.app_background)
                    .into(binding.itemMovieImage);


            if (media.getVip() == 1) binding.moviePremuim.setVisibility(View.VISIBLE);


            binding.movietitle.setText(media.getName());


            binding.rootLayout.setOnClickListener(v -> {

                Intent intent = new Intent(context, StreamingetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                context.startActivity(intent);

            });


        }

    }
}
