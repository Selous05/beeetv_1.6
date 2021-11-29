package com.beeecorptv.ui.moviedetails.adapters;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Download;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.ItemDownloadBinding;
import com.beeecorptv.ui.downloadmanager.core.RepositoryHelper;
import com.beeecorptv.ui.downloadmanager.core.model.data.entity.DownloadInfo;
import com.beeecorptv.ui.downloadmanager.core.settings.SettingsRepository;
import com.beeecorptv.ui.downloadmanager.ui.adddownload.AddDownloadActivity;
import com.beeecorptv.ui.downloadmanager.ui.adddownload.AddDownloadDialog;
import com.beeecorptv.ui.downloadmanager.ui.adddownload.AddInitParams;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.util.Tools;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Adapter for Genre Movies.
 *
 * @author Yobex.
 */
public class DownloadsListAdapter extends RecyclerView.Adapter<DownloadsListAdapter.MainViewHolder> {


    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    private static final String TAG_DOWNLOAD_DIALOG = "add_download_dialog";
    private AddDownloadDialog addDownloadDialog;
    private List<MediaStream> genresList;
    private Download download;
    private MediaRepository mediaRepository;
    private Context context;
    private String mediaGenre;
    private Media media;
    private SettingsManager settingsManager;
    private EasyPlexSupportedHosts easyPlexSupportedHosts;


    public void addToContent(List<MediaStream> mediaList, Download download,
                             Context context,
                             Media media,MediaRepository mediaRepository,SettingsManager settingsManager) {
        this.genresList = mediaList;
        this.download = download;

        this.context = context;
        this.media = media;
        this.mediaRepository = mediaRepository;
        this.settingsManager = settingsManager;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DownloadsListAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemDownloadBinding binding = ItemDownloadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new DownloadsListAdapter.MainViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsListAdapter.MainViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (genresList != null) {
            return genresList.size();
        } else {
            return 0;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {


        private final ItemDownloadBinding binding;


        MainViewHolder(@NonNull ItemDownloadBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }


        @SuppressLint("StaticFieldLeak")
        void onBind(final int position) {

            final MediaStream mediaStream = genresList.get(position);

            binding.movietitle.setText(mediaStream.getServer());

            binding.downloadList.setOnClickListener(view -> {

                if (settingsManager.getSettings().getAllowAdm() == 1) {


                    if (mediaStream.getExternal()  == 1) {

                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mediaStream.getLink())));

                    } else   if (mediaStream.getSupportedHosts() == 1) {


                        easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);
                        easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                            @Override
                            public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                if (multipleQuality) {
                                    if (vidURL != null) {

                                        CharSequence[] name = new CharSequence[vidURL.size()];

                                        for (int i = 0; i < vidURL.size(); i++) {
                                            name[i] = vidURL.get(i).getQuality();
                                        }


                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                        builder.setTitle(context.getString(R.string.select_qualities));
                                        builder.setCancelable(true);
                                        builder.setItems(name, (dialogInterface, i) -> onLoadDonwloadFromDialogs(mediaStream, vidURL.get(i).getUrl()));

                                        builder.show();


                                    } else
                                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {


                                    onLoadDonwloadFromDialogs(mediaStream,vidURL.get(0).getUrl());
                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(mediaStream.getLink());


                    }else {



                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_download_options);
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());

                        lp.gravity = Gravity.BOTTOM;
                        lp.width = MATCH_PARENT;
                        lp.height = MATCH_PARENT;

                        LinearLayout withAdm = dialog.findViewById(R.id.withAdm);
                        LinearLayout withApp = dialog.findViewById(R.id.withApp);
                        LinearLayout with1App = dialog.findViewById(R.id.with1DM);

                        withAdm.setOnClickListener(v12 -> Tools.downloadFromAdm(context,mediaStream.getLink(),true,media,settingsManager));

                        with1App.setOnClickListener(v12 -> Tools.downloadFrom1dm(context,mediaStream.getLink(),true,media,settingsManager));

                        withApp.setOnClickListener(v12 -> {


                            onLoadDownloadApp(mediaStream,mediaStream.getLink());
                            dialog.dismiss();
                        });



                        dialog.show();
                        dialog.getWindow().setAttributes(lp);

                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                dialog.dismiss());


                        dialog.show();
                        dialog.getWindow().setAttributes(lp);

                    }



                } else {

                    if (mediaStream.getExternal()  == 1) {

                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mediaStream.getLink())));


                    }else   if (mediaStream.getSupportedHosts() == 1){

                        easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);
                        easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                            @Override
                            public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                if (multipleQuality) {
                                    if (vidURL != null) {

                                        CharSequence[] name = new CharSequence[vidURL.size()];

                                        for (int i = 0; i < vidURL.size(); i++) {
                                            name[i] = vidURL.get(i).getQuality();
                                        }


                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                        builder.setTitle(context.getString(R.string.select_qualities));
                                        builder.setCancelable(true);
                                        builder.setItems(name, (dialogInterface, i) ->

                                                onLoadDownloadApp(mediaStream,vidURL.get(i).getUrl()));
                                        builder.show();


                                    } else
                                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {


                                    onLoadDownloadApp(mediaStream,vidURL.get(0).getUrl());
                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(mediaStream.getLink());



                    }else {


                        onLoadDownloadApp(mediaStream, mediaStream.getLink());

                    }

                }


            });

        }

        private void onLoadDonwloadFromDialogs(MediaStream mediaStream, String url) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_download_options);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;

            LinearLayout withAdm = dialog.findViewById(R.id.withAdm);
            LinearLayout withApp = dialog.findViewById(R.id.withApp);
            LinearLayout with1App = dialog.findViewById(R.id.with1DM);

            withAdm.setOnClickListener(v12 -> {
                Tools.downloadFromAdm(context, url, true, media, settingsManager);
                dialog.dismiss();
            });
            with1App.setOnClickListener(v12 -> {
                Tools.downloadFrom1dm(context, url, true, media, settingsManager);
                dialog.dismiss();
            });

            withApp.setOnClickListener(v12 -> {

                onLoadDownloadApp(mediaStream,url);
                dialog.dismiss();
            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }


        private void onLoadDownloadApp(MediaStream mediaStream, String downloadUrl) {

            for (Genre genre : media.getGenres()) {

                mediaGenre = genre.getName();
            }

            FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
            addDownloadDialog = (AddDownloadDialog)fm.findFragmentByTag(TAG_DOWNLOAD_DIALOG);
            if (addDownloadDialog == null) {
                AddInitParams initParams = null;
                Intent i = ((FragmentActivity)context).getIntent();
                if (i != null)
                    initParams = i.getParcelableExtra(AddDownloadActivity.TAG_INIT_PARAMS);
                if (initParams == null) {
                    initParams = new AddInitParams();
                }
                fillInitParams(initParams, downloadUrl,mediaStream);
                addDownloadDialog = AddDownloadDialog.newInstance(initParams);
                addDownloadDialog.show(fm, TAG_DOWNLOAD_DIALOG);
            }

            download = new Download(media.getId(),media.getId(),media.getBackdropPath(),media.getTitle(),"");
            download.setType("0");
            download.setVoteAverage(media.getVoteAverage());
            download.setHasrecap(media.getHasrecap());
            download.setSkiprecapStartIn(media.getSkiprecapStartIn());
            download.setExternalId(media.getImdbExternalId());
            download.setHls(mediaStream.getHls());
            download.setMediaGenre(mediaGenre);
            download.setOverview(media.getOverview());

            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addMovie(download))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        }
    }


    private void fillInitParams(AddInitParams params, String downloadUrl, MediaStream mediaStream)
    {
        SettingsRepository pref = RepositoryHelper.getSettingsRepository(context);
        SharedPreferences localPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (params.url == null) {
            params.url = downloadUrl;
        }

        if (mediaStream.getUseragent() !=null && !mediaStream.getUseragent().isEmpty()){

            if (params.userAgent == null) {
                params.userAgent = mediaStream.getUseragent();
            }
        }


        if (mediaStream.getHeader() !=null && !mediaStream.getHeader().isEmpty()){

            if (params.refer == null) {
                params.refer = mediaStream.getHeader();
            }
        }


        if (params.type == null) {
            params.type = "0";
        }


        if (params.mediaId == null) {
            params.mediaId = media.getId();
        }

        if (params.fileName == null) {
            params.fileName = media.getTitle().replaceAll("[^a-zA-Z0-9_-]", "");

        }


        if (params.mediaName == null) {
            params.mediaName = media.getTitle();
        }


        if (params.mediabackdrop == null) {
            params.mediabackdrop = media.getBackdropPath();
        }



        if (params.dirPath == null) {
            params.dirPath = Uri.parse(pref.saveDownloadsIn());
        }
        if (params.retry == null) {
            params.retry = localPref.getBoolean(
                    context.getString(R.string.add_download_retry_flag),
                    true
            );
        }
        if (params.replaceFile == null) {
            params.replaceFile = localPref.getBoolean(
                    context.getString(R.string.add_download_replace_file_flag),
                    false
            );
        }
        if (params.unmeteredConnectionsOnly == null) {
            params.unmeteredConnectionsOnly = localPref.getBoolean(
                    context.getString(R.string.add_download_unmetered_only_flag),
                    false
            );
        }
        if (params.numPieces == null) {
            params.numPieces = localPref.getInt(
                    context.getString(R.string.add_download_num_pieces),
                    DownloadInfo.MIN_PIECES
            );
        }
    }

}
