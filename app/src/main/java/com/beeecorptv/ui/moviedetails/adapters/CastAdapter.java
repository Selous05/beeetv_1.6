package com.beeecorptv.ui.moviedetails.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beeecorptv.data.model.credits.Cast;
import com.beeecorptv.databinding.ListItemCastBinding;
import com.beeecorptv.ui.casts.CastDetailsActivity;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.util.Tools;
import java.util.List;
import javax.inject.Inject;

import static com.beeecorptv.util.Constants.ARG_CAST;


/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> castList;
    private final Context context;
    private final boolean internal;

    @Inject
    SettingsManager settingsManager;


    public CastAdapter(SettingsManager settingsManager, Context context, boolean internal) {

        this.settingsManager = settingsManager;
        this.context = context;
        this.internal = internal;
    }



    public void addCasts(List<Cast> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CastAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ListItemCastBinding binding = ListItemCastBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new CastAdapter.CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdapter.CastViewHolder holder, int position) {
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

    class CastViewHolder extends RecyclerView.ViewHolder {

        private final ListItemCastBinding binding;


        CastViewHolder(@NonNull ListItemCastBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Cast cast = castList.get(position);

            if (internal) {

                Tools.onLoadMediaCover(context,binding.imageCast,cast.getProfilePath());

                binding.castName.setText(cast.getName());

                binding.rootLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(context, CastDetailsActivity.class);
                    intent.putExtra(ARG_CAST, cast);
                    context.startActivity(intent);

                });

            }else {

                Tools.onLoadMediaCover(context,binding.imageCast,settingsManager.getSettings().getImdbCoverPath() + cast.getProfilePath());

                binding.castName.setText(cast.getName());
            }

        }
    }
}
