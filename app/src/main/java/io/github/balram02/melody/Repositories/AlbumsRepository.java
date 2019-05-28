package io.github.balram02.melody.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.balram02.melody.Database.SongsDao;
import io.github.balram02.melody.Database.SongsDatabase;
import io.github.balram02.melody.Models.AlbumsModel;

public class AlbumsRepository {

    private SongsDatabase songsDB;
    private SongsDao songsDao;
    private LiveData<List<AlbumsModel>> songsByAlbum;

    public AlbumsRepository(Application application) {
        songsDB = SongsDatabase.getInstance(application);
        songsDao = songsDB.songDao();
        songsByAlbum = songsDao.getAllSongsByAlbum();
    }

    public LiveData<List<AlbumsModel>> getAllSongsByAlbum() {
        return songsByAlbum;
    }
}
