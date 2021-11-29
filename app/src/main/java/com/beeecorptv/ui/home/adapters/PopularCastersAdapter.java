package com.beeecorptv.ui.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beeecorptv.R;
import com.beeecorptv.data.model.credits.Cast;
import com.beeecorptv.databinding.ItemPopularCastersBinding;
import com.beeecorptv.ui.casts.CastDetailsActivity;
import com.beeecorptv.util.Tools;

import java.util.List;

import static com.beeecorptv.util.Constants.ARG_CAST;


/**
 * Adapter for Movie.
 *
 * @author Yobex.
 */
public class PopularCastersAdapter extends RecyclerView.Adapter<PopularCastersAdapter.MainViewHolder> {

    private List<Cast> castList;
    private Context context;

    public void addMain(List<Cast> mediaList,Context context) {
        this.castList = mediaList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularCastersAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemPopularCastersBinding binding = ItemPopularCastersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new PopularCastersAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularCastersAdapter.MainViewHolder holder, int position) {
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

        private final ItemPopularCastersBinding binding;

        MainViewHolder(@NonNull ItemPopularCastersBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(final int position) {

            final Cast cast = castList.get(position);

            binding.casttitle.setText(cast.getName());


            if (cast.getGender() == 1) {
              binding.mgenres.setText(R.string.actress);
            }else {

                binding.mgenres.setText(R.string.actor);
            }

            Tools.onLoadMediaCoverAdapters(context,binding.itemCastImage, cast.getProfilePath());

            binding.rootLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, CastDetailsActivity.class);
                intent.putExtra(ARG_CAST, cast);
                context.startActivity(intent);

            });

        }

    }

}
