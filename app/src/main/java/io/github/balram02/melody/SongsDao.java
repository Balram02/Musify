package io.github.balram02.melody;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SongsDao {

    @Insert
    void insert(SongsModel songsModel);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(SongsModel songsModel);

    @Delete
    void delete(SongsModel songsModel);

    @Query("SELECT * FROM songs_table ORDER BY title")
    LiveData<List<SongsModel>> getAllSongs();

}
