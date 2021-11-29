package com.beeecorptv.ui.player.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.databinding.RowPlayerMoviesEndedBinding;
import java.io.Serializable;
import java.util.List;


/**
 * Adapter for Next Movie.
 *
 * @author Yobex.
 */
public class NextPlayMoviesAdapter extends RecyclerView.Adapter<NextPlayMoviesAdapter.NextPlayMoviesViewHolder> {

    private List<Media> castList;


    public void addSeasons(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NextPlayMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowPlayerMoviesEndedBinding binding = RowPlayerMoviesEndedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new NextPlayMoviesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NextPlayMoviesViewHolder holder, int position) {
        //
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }



    public Serializable getFirstItem() {
        if (castList != null) {
            return castList.get(0).getId();
        } else {
            return 0;
        }
    }


    static class NextPlayMoviesViewHolder extends RecyclerView.ViewHolder {

        NextPlayMoviesViewHolder(@NonNull RowPlayerMoviesEndedBinding binding) {
            super(binding.getRoot());

        }

    }
}






