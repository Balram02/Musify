package io.github.balram02.musify.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.github.balram02.musify.Models.AlbumsModel;
import io.github.balram02.musify.Models.SongsModel;

@Dao
public interface SongsDao {

    @Insert
    void insert(SongsModel songsModel);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(SongsModel songsModel);

    @Delete
    void delete(SongsModel songsModel);

    @Query("SELECT * FROM songs_table ORDER BY title")
    LiveData<List<SongsModel>> getAllSongs();

    @Query("SELECT album FROM songs_table ORDER BY album")
    LiveData<List<AlbumsModel>> getAllSongsByAlbum();

    @Query("SELECT * FROM songs_table WHERE id IN (SELECT id FROM songs_table ORDER BY RANDOM() LIMIT 70)")
    LiveData<List<SongsModel>> getSongsQueue();

    @Query("SELECT * FROM songs_table WHERE is_favorite = 1 ORDER BY title ")
    LiveData<List<SongsModel>> getFavoriteSongs();

    @Query("SELECT * FROM songs_table ORDER BY title ASC LIMIT 1")
    LiveData<SongsModel> getFirstSong();
}
