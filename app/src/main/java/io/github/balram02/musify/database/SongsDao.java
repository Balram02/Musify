package io.github.balram02.musify.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.github.balram02.musify.models.SongsModel;

@Dao
public interface SongsDao {

    /**************insert operations**************/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SongsModel songsModel);


    /**************update operations**************/

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(SongsModel songsModel);


    /**************delete operations**************/

    @Delete
    void delete(SongsModel songsModel);


    /**************retrieve operations**************/

    @Query("SELECT * FROM songs_table ORDER BY title ASC")
    LiveData<List<SongsModel>> getAllSongs();

    @Query("SELECT * FROM songs_table ORDER BY RANDOM() LIMIT 70")
    List<SongsModel> getShuffleSongsQueue();

    @Query("SELECT * FROM songs_table ORDER BY title ASC")
    List<SongsModel> getAllSongsQueue();

    @Query("SELECT * FROM songs_table WHERE is_favorite = 1 ORDER BY title ")
    LiveData<List<SongsModel>> getFavoriteSongs();

    @Query("SELECT * FROM songs_table WHERE is_favorite = 1 ORDER BY title ")
    List<SongsModel> getFavoritesQueueList();

    @Query("SELECT * FROM songs_table WHERE is_favorite = 1 ORDER BY RANDOM()")
    List<SongsModel> getFavoritesShuffleQueueList();

    @Query("SELECT is_favorite from songs_table WHERE path = :path")
    LiveData<Boolean> isFavorite(String path);

    @Query("SELECT * FROM songs_table WHERE accessed_timestamp != 0 AND accessed_timestamp <= datetime() ORDER BY accessed_timestamp DESC LIMIT 10")
    LiveData<List<SongsModel>> getRecentlyPlayedSongs();

    @Query("SELECT * FROM songs_table GROUP BY album")
    LiveData<List<SongsModel>> getAlbums();

    @Query("SELECT * FROM songs_table GROUP BY artist")
    LiveData<List<SongsModel>> getArtist();

    @Query("SELECT * FROM songs_table WHERE album = :album ORDER BY title ASC")
    LiveData<List<SongsModel>> getSongsByAlbums(String album);

    @Query("SELECT * FROM songs_table WHERE artist = :artist ORDER BY title ASC")
    LiveData<List<SongsModel>> getSongsByArtist(String artist);

    @Query("SELECT * FROM songs_table " + "WHERE title LIKE '%' || :queryText || '%' OR " +
            "album LIKE '%' || :queryText || '%' OR " +
            "artist LIKE '%' || :queryText || '%' LIMIT 10")
    List<SongsModel> getSearchQueryResults(String queryText);
}
