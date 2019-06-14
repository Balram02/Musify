package io.github.balram02.musify.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.github.balram02.musify.models.AlbumsModel;
import io.github.balram02.musify.models.SongsModel;

@Dao
public interface SongsDao {

    /**************insert operations**************/

    @Insert
    void insert(SongsModel songsModel);


    /**************update operations**************/

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(SongsModel songsModel);


    /**************delete operations**************/

    @Delete
    void delete(SongsModel songsModel);


    /**************retrieve operations**************/

    @Query("SELECT * FROM songs_table ORDER BY title")
    LiveData<List<SongsModel>> getAllSongs();

    @Query("SELECT album FROM songs_table ORDER BY album")
    LiveData<List<AlbumsModel>> getAllSongsByAlbum();

    @Query("SELECT * FROM songs_table ORDER BY RANDOM() LIMIT 70")
    List<SongsModel> getShuffleSongsQueue();

    @Query("SELECT * FROM songs_table ORDER BY title ASC")
    List<SongsModel> getAllSongsQueue();

    @Query("SELECT * FROM songs_table WHERE is_favorite = 1 ORDER BY title ")
    LiveData<List<SongsModel>> getFavoriteSongs();

    @Query("SELECT * FROM songs_table WHERE is_favorite = 1 ORDER BY title ")
    List<SongsModel> getFavoriteSongsQueueList();

    @Query("SELECT * FROM songs_table WHERE is_favorite = 1 ORDER BY RANDOM()")
    List<SongsModel> getFavoriteSongsShuffleQueueList();

    @Query("SELECT is_favorite from songs_table WHERE id = :id")
    LiveData<Boolean> isFavorite(int id);

}
