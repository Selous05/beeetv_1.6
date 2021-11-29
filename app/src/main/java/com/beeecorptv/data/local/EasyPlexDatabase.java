package com.beeecorptv.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.beeecorptv.data.local.converters.CastConverter;
import com.beeecorptv.data.local.converters.GenreConverter;
import com.beeecorptv.data.local.converters.MediaStreamConverter;
import com.beeecorptv.data.local.converters.MediaSubstitlesConverter;
import com.beeecorptv.data.local.converters.SaisonConverter;
import com.beeecorptv.data.local.converters.VideosConverter;
import com.beeecorptv.data.local.dao.AnimesDao;
import com.beeecorptv.data.local.dao.MoviesDao;
import com.beeecorptv.data.local.dao.DownloadDao;
import com.beeecorptv.data.local.dao.HistoryDao;
import com.beeecorptv.data.local.dao.ResumeDao;
import com.beeecorptv.data.local.dao.SeriesDao;
import com.beeecorptv.data.local.dao.StreamListDao;
import com.beeecorptv.data.local.entity.Animes;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.local.entity.Download;
import com.beeecorptv.data.local.entity.Series;
import com.beeecorptv.data.local.entity.Stream;
import com.beeecorptv.data.model.media.Resume;


/**
 * The Room database that contains the Favorite Movies & Series & Animes table
 * Define an abstract class that extends RoomDatabase.
 * This class is annotated with @Database, lists the entities contained in the database,
 * and the DAOs which access them.
 */
@Database(entities = {Media.class, Series.class, Animes.class, Download.class, History.class, Stream.class, Resume.class}, version =45, exportSchema = false)
@TypeConverters({GenreConverter.class,
        CastConverter.class,
        VideosConverter.class,
        SaisonConverter.class,
        MediaSubstitlesConverter.class,
        MediaStreamConverter.class})
public abstract class EasyPlexDatabase extends RoomDatabase {

    public abstract MoviesDao favoriteDao();
    public abstract SeriesDao seriesDao();
    public abstract AnimesDao animesDao();
    public abstract DownloadDao progressDao();
    public abstract HistoryDao historyDao();
    public abstract StreamListDao streamListDao();
    public abstract ResumeDao resumeDao();

}
