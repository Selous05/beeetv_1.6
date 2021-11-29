
package com.beeecorptv.ui.downloadmanager.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.beeecorptv.ui.downloadmanager.core.RepositoryHelper;
import com.beeecorptv.ui.downloadmanager.core.model.DownloadScheduler;
import com.beeecorptv.ui.downloadmanager.core.model.data.entity.DownloadInfo;
import com.beeecorptv.ui.downloadmanager.core.storage.DataRepository;

import java.util.UUID;

/*
 * Used only by DownloadScheduler.
 */

public class GetAndRunDownloadWorker extends Worker
{
    public static final String TAG_ID = "id";

    public GetAndRunDownloadWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        Context context = getApplicationContext();
        DataRepository repo = RepositoryHelper.getDataRepository(context);

        String uuid = getInputData().getString(TAG_ID);
        if (uuid == null)
            return Result.failure();

        UUID id;
        try {
            id = UUID.fromString(uuid);

        } catch (IllegalArgumentException e) {
            return Result.failure();
        }

        DownloadInfo info = repo.getInfoById(id);
        if (info == null)
            return Result.failure();

        DownloadScheduler.run(context, info);

        return Result.success();
    }
}
