package com.beeecorptv.ui.upcoming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.beeecorptv.data.model.upcoming.Upcoming;
import com.beeecorptv.databinding.ItemUpcomingBinding;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import timber.log.Timber;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.ui.upcoming.UpcomingTitlesActivity.ARG_MOVIE;

/**
 * Adapter for  Upcoming Movies
 *
 * @author Yobex.
 */
public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder> {

    private List<Upcoming> upcomingList;

    public void addCasts(List<Upcoming> castList) {
        this.upcomingList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemUpcomingBinding binding = ItemUpcomingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new UpcomingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        holder.onBind(position);

    }

    @Override
    public int getItemCount() {
        if (upcomingList != null) {
            return upcomingList.size();
        } else {
            return 0;
        }
    }

    class UpcomingViewHolder extends RecyclerView.ViewHolder {

        private final ItemUpcomingBinding binding;


        UpcomingViewHolder (@NonNull ItemUpcomingBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(final int position) {

            final Upcoming upcoming = upcomingList.get(position);

            Context context = binding.itemMovieImage.getContext();

            Glide.with(context).asBitmap().load(upcoming.getPosterPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .into(binding.itemMovieImage);

            binding.movietitle.setText(upcoming.getTitle());

            if (upcoming.getReleaseDate() != null && !upcoming.getReleaseDate().trim().isEmpty()) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
                try {
                    Date releaseDate = sdf1.parse(upcoming.getReleaseDate());
                    binding.releaseShowCard.setText("Coming "+sdf2.format(releaseDate));
                } catch (ParseException e) {

                    Timber.d("%s", Arrays.toString(e.getStackTrace()));

                }
            } else {
                binding.releaseShowCard.setText("");
            }

            binding.rootLayout.setOnClickListener(v -> {

                Intent intent = new Intent(context, UpcomingTitlesActivity.class);
                intent.putExtra(ARG_MOVIE, upcoming);
                context.startActivity(intent);
                Animatoo.animateFade(context);

            });


        }
    }


}
