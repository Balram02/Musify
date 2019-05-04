package io.github.balram02.melody;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SongsDao {

    @Insert
    void insert(SongsModel songsModel);

    @Update
    void update(SongsModel songsModel);

    @Delete
    void delete(SongsModel songsModel);

    @Query("SELECT * FROM songs_table ORDER BY title")
    LiveData<List<SongsModel>> getAllSongs();

}
