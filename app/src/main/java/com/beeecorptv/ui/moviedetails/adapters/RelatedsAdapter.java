package com.beeecorptv.ui.moviedetails.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.databinding.ItemRelatedsBinding;
import com.beeecorptv.ui.moviedetails.MovieDetailsActivity;
import com.beeecorptv.util.Tools;
import java.util.List;
import static com.beeecorptv.util.Constants.ARG_MOVIE;

/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class RelatedsAdapter extends RecyclerView.Adapter<RelatedsAdapter.MainViewHolder> {

    private List<Media> castList;

    @SuppressLint("NotifyDataSetChanged")
    public void addToContent(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RelatedsAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemRelatedsBinding binding = ItemRelatedsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new RelatedsAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedsAdapter.MainViewHolder holder, int position) {
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

            binding.movieName.setText(related.getTitle());



            binding.rootLayout.setOnClickListener(v -> {
                ((Activity)context).finish();
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, related);
                context.startActivity(intent);

            });

            Tools.onLoadMediaCover(context,binding.imageMovie,related.getPosterPath());


        }
    }
}
