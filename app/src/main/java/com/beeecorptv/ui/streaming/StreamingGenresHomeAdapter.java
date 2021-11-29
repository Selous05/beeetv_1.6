package com.beeecorptv.ui.streaming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.databinding.ItemShowStreamingHomeBinding;
import com.beeecorptv.util.GlideApp;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.util.Constants.ARG_MOVIE;

public class StreamingGenresHomeAdapter extends PagedListAdapter<Media, StreamingGenresHomeAdapter.ItemViewHolder> {

    private final Context context;

    public StreamingGenresHomeAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemShowStreamingHomeBinding binding = ItemShowStreamingHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new StreamingGenresHomeAdapter.ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        holder.onBind(position);


        }


    private static final DiffUtil.ItemCallback<Media> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Media>() {
                @Override
                public boolean areItemsTheSame(Media oldItem, Media newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Media oldItem, Media newItem) {
                    return oldItem.equals(newItem);
                }
            };



    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemShowStreamingHomeBinding binding;


        ItemViewHolder(@NonNull ItemShowStreamingHomeBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = getItem(position);

            GlideApp.with(context).asBitmap().load(media.getPosterPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .placeholder(R.color.app_background)
                    .into(binding.imageViewShowCard);

            binding.movietitle.setText(media.getName());


            for (Genre genre : media.getGenres()) {
                binding.streamOverview.setText(genre.getName());
            }


            binding.rootLayout.setOnClickListener(v -> {

                Intent intent = new Intent(context, StreamingetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                context.startActivity(intent);


            });


        }

    }
}
