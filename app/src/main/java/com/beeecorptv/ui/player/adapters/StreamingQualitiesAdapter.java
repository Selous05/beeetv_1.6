package com.beeecorptv.ui.player.adapters;

import static com.beeecorptv.util.Constants.SERVER_BASE_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.R;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.databinding.RowSubstitleBinding;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.ui.player.activities.EmbedActivity;
import com.beeecorptv.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Movie Qualities.
 *
 * @author Yobex.
 */
public class StreamingQualitiesAdapter extends RecyclerView.Adapter<StreamingQualitiesAdapter.CastViewHolder> {

    private List<MediaStream> mediaStreams;
    private MediaModel mMediaModel;
    ClickDetectListner clickDetectListner;
    private int qualitySelected;
    private SettingsManager settingsManager;
    private Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void addSeasons(List<MediaStream> castList, ClickDetectListner clickDetectListner, SettingsManager settingsManager, Context context) {
        this.mediaStreams = castList;
        this.settingsManager = settingsManager;
        this.context = context;
        notifyDataSetChanged();
        this.clickDetectListner = clickDetectListner;

    }

    @NonNull
    @Override
    public StreamingQualitiesAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowSubstitleBinding binding = RowSubstitleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new StreamingQualitiesAdapter.CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamingQualitiesAdapter.CastViewHolder holder, int position) {
        holder.onBind(position);
    }



    @Override
    public int getItemCount() {
        if (mediaStreams != null) {
            return mediaStreams.size();
        } else {
            return 0;
        }
    }

    class CastViewHolder extends RecyclerView.ViewHolder {

        private final RowSubstitleBinding binding;

        CastViewHolder (@NonNull RowSubstitleBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @SuppressLint({"SetTextI18n", "StaticFieldLeak"})
        void onBind(final int position) {

            final MediaStream mediaStream = mediaStreams.get(position);

            binding.eptitle.setText(mediaStream.getLang() + " - "+mediaStream.getServer());

            if (mediaStream.getLinkpremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);

            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }


            binding.eptitle.setOnClickListener(v -> {


                if (mediaStream.getEmbed() == 1) {


                    startStreamFromEmbed(mediaStream.getLink());


                }else if (mediaStream.getSupportedHosts() == 1 ) {

                    startStreamFromSupportedHosts(mediaStream);


                }else {


                    startStreamFromPlayer(mediaStream);
                }




            });

        }

        private void startStreamFromSupportedHosts(MediaStream mediaStream) {

            EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

            if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
            }

            easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                    if (multipleQuality){
                        if (vidURL!=null) {

                            CharSequence[] names = new CharSequence[vidURL.size()];

                            for (int i = 0; i < vidURL.size(); i++) {
                                names[i] = vidURL.get(i).getQuality();
                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                            builder.setTitle(context.getString(R.string.select_qualities));
                            builder.setCancelable(true);
                            builder.setItems(names, (dialogInterface, i) -> {

                                String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
                                String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
                                String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
                                String artwork = (String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
                                String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
                                mMediaModel = MediaModel.media(id,null,currentQuality,type,name, vidURL.get(i).getUrl(), artwork, null,null,null
                                        ,null,null,null,
                                        null,
                                        null,null,null,
                                        mediaStream.getHls(),null,null
                                        ,null,0
                                        ,0
                                        ,null,null,0);
                                ((EasyPlexMainPlayer)context).update(mMediaModel);
                                clickDetectListner.onQualityClicked(true);

                            });

                            builder.show();



                        }else  Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                    }else {


                        String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
                        String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
                        String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
                        String artwork = (String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
                        String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
                        mMediaModel = MediaModel.media(id,null,currentQuality,type,name, vidURL.get(0).getUrl(), artwork, null,null,null
                                ,null,null,null,
                                null,
                                null,null,null,
                                mediaStream.getHls(),null,null
                                ,null,0
                                ,0
                                ,null,null,0);
                        ((EasyPlexMainPlayer)context).update(mMediaModel);
                        clickDetectListner.onQualityClicked(true);
                    }

                }

                @Override
                public void onError() {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            easyPlexSupportedHosts.find(mediaStream.getLink());
        }

        private void startStreamFromPlayer(MediaStream mediaStream) {

            String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
            String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
            String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
            String artwork = (String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
            String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
            mMediaModel = MediaModel.media(id,null,currentQuality,type,name, mediaStream.getLink(), artwork, null,null,null
                    ,null,null,null,
                    null,
                    null,null,null,
                    mediaStream.getHls(),null,null
                    ,null,0
                    ,0
                    ,null,null,0);
            ((EasyPlexMainPlayer)context).update(mMediaModel);
            clickDetectListner.onQualityClicked(true);
        }

        private void startStreamFromEmbed(String link) {

            Intent intent = new Intent(context, EmbedActivity.class);
            intent.putExtra(Constants.MOVIE_LINK, link);
            context.startActivity(intent);
        }
    }


}
