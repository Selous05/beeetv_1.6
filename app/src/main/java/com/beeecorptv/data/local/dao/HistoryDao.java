package com.beeecorptv.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.beeecorptv.data.local.entity.History;
import java.util.List;
import io.reactivex.Flowable;

/**
 * Data Access Object that contains methods used for accessing the database.
 *
 * @author Yobex.
 */
@Dao
public interface HistoryDao {

    // Return Movies & Series From Favorite Table
    @Query("SELECT * FROM history ORDER BY createdAt DESC")
    Flowable<List<History>> getHistory();


    // Save the the movie or serie in the  Favorite Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMediaToFavorite(History history);


    @Query("DELETE FROM history")
    void deleteHistory();



    // Return true if the element in the featured is in the  Favorite Table
    @Query("SELECT * FROM history WHERE id=:id")
    boolean hasHistory(int id);



    @Query("SELECT * FROM history WHERE id=:id AND type_history=:type")
    LiveData<History> hasHistory2(int id,String type);


    @Delete
    void deleteMediaFromHistory(History mediaDetail);


}
