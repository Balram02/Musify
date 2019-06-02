package io.github.balram02.musify.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.balram02.musify.Database.SongsDao;
import io.github.balram02.musify.Database.SongsDatabase;
import io.github.balram02.musify.Models.AlbumsModel;

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
