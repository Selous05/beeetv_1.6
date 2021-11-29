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
import com.beeecorptv.databinding.ItemPinnedBinding;
import com.beeecorptv.ui.animes.AnimeDetailsActivity;
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
public class PinnedAdapter extends RecyclerView.Adapter<PinnedAdapter.MainViewHolder> {

    private List<Media> castList;
    private Context context;

    public void addMain(List<Media> mediaList,Context context) {
        this.castList = mediaList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PinnedAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemPinnedBinding binding = ItemPinnedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new PinnedAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PinnedAdapter.MainViewHolder holder, int position) {
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

        private final ItemPinnedBinding binding;

        MainViewHolder(@NonNull ItemPinnedBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = castList.get(position);


            if (media.getType().equals("anime")) {

                binding.movietitle.setText(media.getName());

                if (media.getNewEpisodes() == 1) {

                    binding.hasNewEpisodes.setVisibility(View.VISIBLE);
                }


                binding.rootLayout.setOnLongClickListener(v -> {
                    Toast.makeText(context, ""+media.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                });

                binding.rootLayout.setOnClickListener(view -> {


                    Intent intent = new Intent(context, AnimeDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                });



            }else   if (media.getType().equals("serie"))  {



                binding.movietitle.setText(media.getName());

                if (media.getNewEpisodes() == 1) {

                    binding.hasNewEpisodes.setVisibility(View.VISIBLE);
                }


                binding.rootLayout.setOnLongClickListener(v -> {
                    Toast.makeText(context, ""+media.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                });

                binding.rootLayout.setOnClickListener(view -> {


                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                });


            }else  {


                binding.rootLayout.setOnLongClickListener(v -> {
                    Toast.makeText(context, ""+media.getTitle(), Toast.LENGTH_SHORT).show();
                    return false;
                });

                binding.rootLayout.setOnClickListener(view -> {


                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                });

                binding.movietitle.setText(media.getTitle());

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
