package com.beeecorptv.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.databinding.RowItemChoosedBinding;
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
public class ChoosedAdapter extends RecyclerView.Adapter<ChoosedAdapter.MainViewHolder> {

    private List<Media> castList;
    private Context context;

    public void addMain(List<Media> mediaList,Context context) {
        this.castList = mediaList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChoosedAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowItemChoosedBinding binding = RowItemChoosedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ChoosedAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoosedAdapter.MainViewHolder holder, int position) {
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

        private final RowItemChoosedBinding binding;

        MainViewHolder(@NonNull RowItemChoosedBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }


        void onBind(final int position) {

            final Media media = castList.get(position);


            if (media.getIsAnime() == 1) {

                binding.movietitle.setText(media.getName());


            } else if (media.getName() !=null) {

                binding.movietitle.setText(media.getName());


            }else {

                binding.movietitle.setText(media.getTitle());


            }

            binding.rootLayout.setOnClickListener(v -> {

                if (media.getIsAnime() == 1) {


                    binding.movietitle.setText(media.getName());

                    Intent intent = new Intent(context, AnimeDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                } else if (media.getName() !=null) {

                    binding.movietitle.setText(media.getName());

                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);

                }else {


                    binding.movietitle.setText(media.getTitle());

                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);

                }
            });

            if (media.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }
            Tools.onLoadMediaCoverAdapters(context,binding.itemMovieImage, media.getPosterPath());

        }
    }

}
