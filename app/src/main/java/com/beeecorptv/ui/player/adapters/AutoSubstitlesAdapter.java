package com.beeecorptv.ui.player.adapters;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.beeecorptv.R;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.substitles.Opensub;
import com.beeecorptv.databinding.RowSubstitleBinding;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.util.DownloadFileAsync;
import com.google.android.exoplayer2.util.Log;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.util.List;

import static com.beeecorptv.util.Constants.SUBSTITLE_LOCATION;
import static com.beeecorptv.util.Constants.SUBSTITLE_SUB_FILENAME_ZIP;
import static com.beeecorptv.util.Constants.ZIP_FILE_NAME;
import static com.beeecorptv.util.Constants.ZIP_FILE_NAME2;
import static com.beeecorptv.util.Constants.ZIP_FILE_NAME3;
import static java.lang.String.valueOf;

/**
 * Adapter for Movie or Serie Substitles.
 *
 * @author Yobex.
 */
public class AutoSubstitlesAdapter extends RecyclerView.Adapter<AutoSubstitlesAdapter.SubstitlesViewHolder> {

    private List<Opensub> mediaSubstitles;
    private MediaModel mMediaModel;
    private Context context;
    ClickDetectListner clickDetectListner;




    public void addSubtitle(List<Opensub> castList, ClickDetectListner clickDetectListner,Context context) {
        this.mediaSubstitles = castList;
        notifyDataSetChanged();
        this.clickDetectListner = clickDetectListner;
        this.context = context;

    }

    @NonNull
    @Override
    public SubstitlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowSubstitleBinding binding = RowSubstitleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SubstitlesViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull SubstitlesViewHolder holder, int position) {
        holder.onBind(position);
    }



    @Override
    public int getItemCount() {
        if (mediaSubstitles != null) {
            return mediaSubstitles.size();
        } else {
            return 0;
        }
    }

    class SubstitlesViewHolder extends RecyclerView.ViewHolder {

        private final RowSubstitleBinding binding;

        SubstitlesViewHolder (@NonNull RowSubstitleBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        void onBind(final int position) {

            final Opensub mediaSubstitle = mediaSubstitles.get(position);



            binding.eptitle.setText(mediaSubstitle.getLanguageName());

            binding.eptitle.setOnClickListener(v -> {


                if (((EasyPlexMainPlayer)context).getPlayerController().getMediaType().equals("0")) {

                    DownloadFileAsync download = new DownloadFileAsync(
                            context
                                    .getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath())
                                    +SUBSTITLE_SUB_FILENAME_ZIP, file -> {
                        Log.i("TAG", "file download completed");
                        // check unzip file now
                        ZipFile zipFile;
                        zipFile = new ZipFile("subs.zip");
                        FileHeader fileHeader;
                        fileHeader = zipFile.getFileHeader(
                                context.getExternalFilesDir(Environment.getDataDirectory()
                                        .getAbsolutePath())+SUBSTITLE_SUB_FILENAME_ZIP);
                        if (fileHeader != null) {
                            zipFile.removeFile(fileHeader);
                        }else {
                            if ("srt".equals(mediaSubstitle.getSubFormat())) {
                                new ZipFile(file, null).extractFile(mediaSubstitle.getSubFileName(),
                                        valueOf(context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                        , ZIP_FILE_NAME);
                                Log.i("TAG", "file unzip completed");
                            } else if ("vtt".equals(mediaSubstitle.getSubFormat())) {
                                new ZipFile(file, null).extractFile(mediaSubstitle.getSubFileName(),
                                        valueOf(context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                        , ZIP_FILE_NAME2);
                                Log.i("TAG", "file unzip completed");
                            } else if ("ssa".equals(mediaSubstitle.getSubFormat())) {
                                new ZipFile(file, null).extractFile(mediaSubstitle.getSubFileName(),
                                        valueOf(context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                        , ZIP_FILE_NAME3);
                                Log.i("TAG", "file unzip completed");
                            }


                        }

                    });

                    download.execute(mediaSubstitle.getZipDownloadLink());

                    Toast.makeText(context, "The "+ mediaSubstitle.getLanguageName()+context.getString(R.string.ready_5sec), Toast.LENGTH_LONG).show();

                    clickDetectListner.onSubstitleClicked(true);

                    if ("srt".equals(mediaSubstitle.getSubFormat())) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                            String subs = SUBSTITLE_LOCATION + context.getPackageName() + "/files/data/" + ZIP_FILE_NAME;

                            String substitleLanguage = mediaSubstitle.getLanguageName();

                            String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
                            String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
                            String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
                            String artwork = (valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
                            String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
                            String videoUrl = (valueOf(((EasyPlexMainPlayer)context).getPlayerController().getVideoUrl())) ;
                            mMediaModel = MediaModel.media(id,substitleLanguage,currentQuality,type,name, videoUrl, artwork,
                                    subs,null,null
                                    ,null,null,null,null,null,
                                    null,((EasyPlexMainPlayer)context).getPlayerController().isMediaPremuim()
                                    , ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHlsFormat(),"srt",((EasyPlexMainPlayer)context).getPlayerController().getCurrentExternalId(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getMediaCoverHistory(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentHasRecap(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentStartRecapIn()
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getMediaGenre(),null,((EasyPlexMainPlayer)context).getPlayerController().getVoteAverage());
                            ((EasyPlexMainPlayer)context).update(mMediaModel);
                            ((EasyPlexMainPlayer)context).getPlayerController().isSubtitleEnabled(true);
                            clickDetectListner.onSubstitleClicked(true);
                            ((EasyPlexMainPlayer)context).getPlayerController().subtitleCurrentLang(substitleLanguage);

                        }, 5000);
                    } else if ("vtt".equals(mediaSubstitle.getSubFormat())) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                            String subs = SUBSTITLE_LOCATION + context.getPackageName() + "/files/data/" + ZIP_FILE_NAME2;


                            String substitleLanguage = mediaSubstitle.getLanguageName();

                            String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
                            String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
                            String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
                            String artwork = (valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
                            String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
                            String videoUrl = (valueOf(((EasyPlexMainPlayer)context).getPlayerController().getVideoUrl())) ;
                            mMediaModel = MediaModel.media(id,substitleLanguage,currentQuality,type,name, videoUrl, artwork,
                                    subs,null,null
                                    ,null,null,null,null,null,
                                    null,((EasyPlexMainPlayer)context).getPlayerController().isMediaPremuim()
                                    , ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHlsFormat(),"vtt",((EasyPlexMainPlayer)context).getPlayerController().getCurrentExternalId(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getMediaCoverHistory(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentHasRecap(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentStartRecapIn()
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getMediaGenre(),null,((EasyPlexMainPlayer)context).getPlayerController().getVoteAverage());
                            ((EasyPlexMainPlayer)context).update(mMediaModel);
                            ((EasyPlexMainPlayer)context).getPlayerController().isSubtitleEnabled(true);
                            clickDetectListner.onSubstitleClicked(true);
                            ((EasyPlexMainPlayer)context).getPlayerController().subtitleCurrentLang(substitleLanguage);

                        }, 5000);
                    } else if ("ssa".equals(mediaSubstitle.getSubFormat())) {

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                            String subs = SUBSTITLE_LOCATION + context.getPackageName() + "/files/data/" + ZIP_FILE_NAME3;

                            String substitleLanguage = mediaSubstitle.getLanguageName();

                            String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
                            String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
                            String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
                            String artwork = (valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
                            String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
                            String videoUrl = (valueOf(((EasyPlexMainPlayer)context).getPlayerController().getVideoUrl())) ;
                            mMediaModel = MediaModel.media(id,substitleLanguage,currentQuality,type,name, videoUrl, artwork,
                                    subs,null,null
                                    ,null,null,null,null,null,
                                    null,((EasyPlexMainPlayer)context).getPlayerController().isMediaPremuim()
                                    , ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHlsFormat(),"ssa",((EasyPlexMainPlayer)context).getPlayerController().getCurrentExternalId(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getMediaCoverHistory(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentHasRecap(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentStartRecapIn()
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getMediaGenre(),null,((EasyPlexMainPlayer)context).getPlayerController().getVoteAverage());
                            ((EasyPlexMainPlayer)context).update(mMediaModel);
                            ((EasyPlexMainPlayer)context).getPlayerController().isSubtitleEnabled(true);
                            clickDetectListner.onSubstitleClicked(true);
                            ((EasyPlexMainPlayer)context).getPlayerController().subtitleCurrentLang(substitleLanguage);

                        }, 5000);
                    }


                }else {




                    DownloadFileAsync download = new DownloadFileAsync(
                            context
                                    .getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath())
                                    +SUBSTITLE_SUB_FILENAME_ZIP, file -> {
                        Log.i("TAG", "file download completed");
                        // check unzip file now
                        ZipFile zipFile;
                        zipFile = new ZipFile("subs.zip");
                        FileHeader fileHeader;
                        fileHeader = zipFile.getFileHeader(
                                context.getExternalFilesDir(Environment.getDataDirectory()
                                        .getAbsolutePath())+SUBSTITLE_SUB_FILENAME_ZIP);
                        if (fileHeader != null) {
                            zipFile.removeFile(fileHeader);
                        }else {
                            if ("srt".equals(mediaSubstitle.getSubFormat())) {
                                new ZipFile(file, null).extractFile(mediaSubstitle.getSubFileName(),
                                        valueOf(context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                        , ZIP_FILE_NAME);
                                Log.i("TAG", "file unzip completed");
                            } else if ("vtt".equals(mediaSubstitle.getSubFormat())) {
                                new ZipFile(file, null).extractFile(mediaSubstitle.getSubFileName(),
                                        valueOf(context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                        , ZIP_FILE_NAME2);
                                Log.i("TAG", "file unzip completed");
                            } else if ("ssa".equals(mediaSubstitle.getSubFormat())) {
                                new ZipFile(file, null).extractFile(mediaSubstitle.getSubFileName(),
                                        valueOf(context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                        , ZIP_FILE_NAME3);
                                Log.i("TAG", "file unzip completed");
                            }


                        }

                    });

                    download.execute(mediaSubstitle.getZipDownloadLink());

                    Toast.makeText(context, "The "+ mediaSubstitle.getLanguageName()+context.getString(R.string.ready_5sec), Toast.LENGTH_LONG).show();

                    clickDetectListner.onSubstitleClicked(true);

                    if ("srt".equals(mediaSubstitle.getSubFormat())) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                            String subs = SUBSTITLE_LOCATION + context.getPackageName() + "/files/data/" + ZIP_FILE_NAME;

                            String substitleLanguage = mediaSubstitle.getLanguageName();

                            String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
                            String externalId = ((EasyPlexMainPlayer)context).getPlayerController().getMediaSubstitleName();
                            String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
                            String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
                            String artwork = (String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
                            String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
                            mMediaModel = MediaModel.media(id,externalId,currentQuality,type,name, String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getVideoUrl()), artwork,
                                    subs,Integer.parseInt(((EasyPlexMainPlayer)context).getPlayerController().getEpID()),null
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpTmdbNumber(),((EasyPlexMainPlayer)context).getPlayerController().getSeaonNumber(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getEpName(),((EasyPlexMainPlayer)context).getPlayerController().getSeaonNumber(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpisodePosition(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpTmdbNumber(),((EasyPlexMainPlayer)context).getPlayerController().isMediaPremuim(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHlsFormat(),
                                    "srt",((EasyPlexMainPlayer)context).getPlayerController().getCurrentExternalId()
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getMediaCoverHistory(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHasRecap(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentStartRecapIn(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getMediaGenre(),((EasyPlexMainPlayer)context).getPlayerController().getSerieName(),((EasyPlexMainPlayer)context).getPlayerController().getVoteAverage());
                            ((EasyPlexMainPlayer)context).update(mMediaModel);
                            ((EasyPlexMainPlayer)context).getPlayerController().isSubtitleEnabled(true);
                            clickDetectListner.onSubstitleClicked(true);
                            ((EasyPlexMainPlayer)context).getPlayerController().subtitleCurrentLang(substitleLanguage);

                        }, 5000);
                    } else if ("vtt".equals(mediaSubstitle.getSubFormat())) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                            String subs = SUBSTITLE_LOCATION + context.getPackageName() + "/files/data/" + ZIP_FILE_NAME2;


                            String substitleLanguage = mediaSubstitle.getLanguageName();

                            String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
                            String externalId = ((EasyPlexMainPlayer)context).getPlayerController().getMediaSubstitleName();
                            String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
                            String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
                            String artwork = (String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
                            String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
                            mMediaModel = MediaModel.media(id,externalId,currentQuality,type,name, String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getVideoUrl()), artwork,
                                    subs,Integer.parseInt(((EasyPlexMainPlayer)context).getPlayerController().getEpID()),null
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpTmdbNumber(),((EasyPlexMainPlayer)context).getPlayerController().getSeaonNumber(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getEpName(),((EasyPlexMainPlayer)context).getPlayerController().getSeaonNumber(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpisodePosition(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpTmdbNumber(),((EasyPlexMainPlayer)context).getPlayerController().isMediaPremuim(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHlsFormat(),
                                    "vtt",((EasyPlexMainPlayer)context).getPlayerController().getCurrentExternalId()
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getMediaCoverHistory(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHasRecap(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentStartRecapIn(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getMediaGenre(),((EasyPlexMainPlayer)context).getPlayerController().getSerieName(),((EasyPlexMainPlayer)context).getPlayerController().getVoteAverage());
                            ((EasyPlexMainPlayer)context).update(mMediaModel);
                            ((EasyPlexMainPlayer)context).getPlayerController().isSubtitleEnabled(true);
                            clickDetectListner.onSubstitleClicked(true);
                            ((EasyPlexMainPlayer)context).getPlayerController().subtitleCurrentLang(substitleLanguage);

                        }, 5000);
                    } else if ("ssa".equals(mediaSubstitle.getSubFormat())) {

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                            String subs = SUBSTITLE_LOCATION + context.getPackageName() + "/files/data/" + ZIP_FILE_NAME3;

                            String substitleLanguage = mediaSubstitle.getLanguageName();

                            String id = ((EasyPlexMainPlayer)context).getPlayerController().getVideoID();
                            String externalId = ((EasyPlexMainPlayer)context).getPlayerController().getMediaSubstitleName();
                            String type = ((EasyPlexMainPlayer)context).getPlayerController().getMediaType();
                            String currentQuality = ((EasyPlexMainPlayer)context).getPlayerController().getVideoCurrentQuality();
                            String artwork = (String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getMediaPoster())) ;
                            String name = ((EasyPlexMainPlayer)context).getPlayerController().getCurrentVideoName();
                            mMediaModel = MediaModel.media(id,externalId,currentQuality,type,name, String.valueOf(((EasyPlexMainPlayer)context).getPlayerController().getVideoUrl()), artwork,
                                    subs,Integer.parseInt(((EasyPlexMainPlayer)context).getPlayerController().getEpID()),null
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpTmdbNumber(),((EasyPlexMainPlayer)context).getPlayerController().getSeaonNumber(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getEpName(),((EasyPlexMainPlayer)context).getPlayerController().getSeaonNumber(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpisodePosition(),((EasyPlexMainPlayer)context).getPlayerController().getCurrentEpTmdbNumber(),((EasyPlexMainPlayer)context).getPlayerController().isMediaPremuim(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHlsFormat(),
                                    "ssa",((EasyPlexMainPlayer)context).getPlayerController().getCurrentExternalId()
                                    ,((EasyPlexMainPlayer)context).getPlayerController().getMediaCoverHistory(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentHasRecap(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getCurrentStartRecapIn(),
                                    ((EasyPlexMainPlayer)context).getPlayerController().getMediaGenre(),((EasyPlexMainPlayer)context).getPlayerController().getSerieName(),((EasyPlexMainPlayer)context).getPlayerController().getVoteAverage());
                            ((EasyPlexMainPlayer)context).update(mMediaModel);
                            ((EasyPlexMainPlayer)context).getPlayerController().isSubtitleEnabled(true);
                            clickDetectListner.onSubstitleClicked(true);
                            ((EasyPlexMainPlayer)context).getPlayerController().subtitleCurrentLang(substitleLanguage);

                        }, 5000);
                    }

                }

            });


        }
    }


}
