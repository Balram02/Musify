package io.github.balram02.musify.repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.balram02.musify.database.SongsDao;
import io.github.balram02.musify.database.SongsDatabase;
import io.github.balram02.musify.models.SongsModel;

import static io.github.balram02.musify.constants.Constants.TAG;

public class SongsRepository {

    private final static int INSERT = 1;
    private final static int UPDATE = 2;
    private final static int DELETE = 3;

    private SongsDatabase songsDB;
    private static SongsDao songsDao;
    private LiveData<List<SongsModel>> songs;
    private LiveData<List<SongsModel>> recentSongs;
    private LiveData<List<SongsModel>> songsByAlbums;
    private LiveData<List<SongsModel>> songsByArtist;


    public SongsRepository(Application application) {
        songsDB = SongsDatabase.getInstance(application);
        songsDao = songsDB.songDao();
        songs = songsDao.getAllSongs();
        recentSongs = songsDao.getRecentlyPlayedSongs();
        songsByAlbums = songsDao.getAlbums();
        songsByArtist = songsDao.getArtist();
    }

    public void insert(SongsModel songsModel) {
        performTask(INSERT, songsModel);
    }

    public void update(SongsModel songsModel) {
        performTask(UPDATE, songsModel);
    }

    public void delete(SongsModel songsModel) {
        performTask(DELETE, songsModel);
    }

    public LiveData<List<SongsModel>> getAllSongs() {
        return songs;
    }

    public List<SongsModel> getAllSongsQueue() {
        return songsDao.getAllSongsQueue();
    }

    public List<SongsModel> getShuffleSongsQueue() {
        return songsDao.getShuffleSongsQueue();
    }

    public LiveData<List<SongsModel>> getFavoriteSong() {
        return songsDao.getFavoriteSongs();
    }

    public List<SongsModel> getFavoritesQueueList() {
        return songsDao.getFavoritesQueueList();
    }

    public List<SongsModel> getFavoritesShuffleQueueList() {
        return songsDao.getFavoritesShuffleQueueList();
    }

    public LiveData<Boolean> isFavorite(String path) {
        return songsDao.isFavorite(path);
    }

    public LiveData<List<SongsModel>> getRecentlyPlayedSongs() {
        return recentSongs;
    }

    public LiveData<List<SongsModel>> getAlbums() {
        return songsByAlbums;
    }

    public LiveData<List<SongsModel>> getArtist() {
        return songsByArtist;
    }

    public LiveData<List<SongsModel>> getSongsByAlbums(String albumName) {
        return songsDao.getSongsByAlbums(albumName);
    }

    public LiveData<List<SongsModel>> getSongsByArtist(String artistName) {
        return songsDao.getSongsByArtist(artistName);
    }

    public List<SongsModel> getSearchQueryResults(String queryText) {
        return songsDao.getSearchQueryResults(queryText);
    }

    private void performTask(int operation, SongsModel songsModel) {
        try {
            new DBAsyncTask(operation).execute(songsModel).get();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private static class DBAsyncTask extends AsyncTask<SongsModel, Void, Void> {

        private int operation;

        DBAsyncTask(int operation) {
            this.operation = operation;
        }

        @Override
        protected Void doInBackground(SongsModel... models) {
            switch (operation) {
                case INSERT:
                    songsDao.insert(models[0]);
                    break;
                case UPDATE:
                    songsDao.update(models[0]);
                    break;
                case DELETE:
                    songsDao.delete(models[0]);
                    break;
            }
            return null;
        }
    }

}
