package com.beeecorptv.ui.mylist;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.beeecorptv.util.Constants.ARG_MOVIE;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.media.StatusFav;
import com.beeecorptv.data.repository.AuthRepository;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.ItemShowStreamingHomeBinding;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.streaming.StreamingetailsActivity;
import com.beeecorptv.util.Tools;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class StreamOnlineMyListdapter extends RecyclerView.Adapter<StreamOnlineMyListdapter.MainViewHolder> {

    private final DeleteFavoriteDetectListner deleteFavoriteDetectListner;

    private List<Media> castList;
    private Context context;
    private final MediaRepository mediaRepository;
    private final SettingsManager settingsManager;
    private final AuthRepository authRepository;

    public StreamOnlineMyListdapter(MediaRepository mediaRepository, SettingsManager settingsManager
            ,DeleteFavoriteDetectListner deleteFavoriteDetectListner,AuthRepository authRepository) {
        this.mediaRepository = mediaRepository;
        this.settingsManager = settingsManager;
        this.deleteFavoriteDetectListner = deleteFavoriteDetectListner;
        this.authRepository = authRepository;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addToContent(List<Media> castList, Context context) {
        this.castList = castList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StreamOnlineMyListdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemShowStreamingHomeBinding binding = ItemShowStreamingHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new StreamOnlineMyListdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamOnlineMyListdapter.MainViewHolder holder, int position) {
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

        private final ItemShowStreamingHomeBinding binding;


        MainViewHolder(@NonNull ItemShowStreamingHomeBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(final int position) {

            final Media media = castList.get(position);

            for (Genre genre : media.getGenres()) {
                binding.streamOverview.setText(genre.getName());
            }

            binding.movietitle.setText(media.getName());

            binding.deleteFromHistory.setOnClickListener(v -> {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_remove_movie_from_history);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());

                lp.gravity = Gravity.BOTTOM;
                lp.width = MATCH_PARENT;
                lp.height = MATCH_PARENT;

                TextView movieName = dialog.findViewById(R.id.movietitle);
                TextView movieoverview = dialog.findViewById(R.id.text_overview_label);

                movieName.setText(media.getName());

                movieoverview.setText(context.getString(R.string.are_you_sure_to_delete_from_your_watching_history)+" "+media.getName()+context.getString(R.string.from_your_lists));

                dialog.findViewById(R.id.view_delete_from_history).setOnClickListener(v12 -> {

                    authRepository.getDeleteStreamingOnline(media.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .cache()
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(@NotNull StatusFav statusFav) {

                                    Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(@NotNull Throwable e) {

                                    //
                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });

                    dialog.dismiss();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> deleteFavoriteDetectListner.onMediaDeletedSuccess(true), 300);
                });


                dialog.findViewById(R.id.text_view_cancel).setOnClickListener(v1 -> dialog.dismiss());

                dialog.show();
                dialog.getWindow().setAttributes(lp);

                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->
                        dialog.dismiss());
                dialog.show();
                dialog.getWindow().setAttributes(lp);

            });

            binding.rootLayout.setOnClickListener(view -> mediaRepository.getStream(media.getId(),settingsManager.getSettings().getApiKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Media movieDetail) {

                            Intent intent = new Intent(context, StreamingetailsActivity.class);
                            intent.putExtra(ARG_MOVIE, movieDetail);
                            context.startActivity(intent);


                        }


                        @Override
                        public void onError(@NotNull Throwable e) {

                            //
                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    }));



            Tools.onLoadMediaCover(context,binding.imageViewShowCard,  media.getBackdropPath() == null ? "" : media.getPosterPath());



        }
    }
}
