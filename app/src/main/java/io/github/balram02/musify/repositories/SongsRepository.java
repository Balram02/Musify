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
    private SongsDao songsDao;
    private LiveData<List<SongsModel>> songs;
    private LiveData<List<SongsModel>> songsQueue;
    private LiveData<List<SongsModel>> favoriteSongs;

    public SongsRepository(Application application) {
        songsDB = SongsDatabase.getInstance(application);
        songsDao = songsDB.songDao();
        songs = songsDao.getAllSongs();
        songsQueue = songsDao.getSongsQueue();
        favoriteSongs = songsDao.getFavoriteSongs();
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

    public LiveData<List<SongsModel>> getSongsQueue() {
        return songsQueue;
    }

    public LiveData<List<SongsModel>> getFavoriteSong() {
        return favoriteSongs;
    }

    private void performTask(int operation, SongsModel songsModel) {
        try {
            new DBAsyncTask(songsDao, operation).execute(songsModel).get();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private static class DBAsyncTask extends AsyncTask<SongsModel, Void, Void> {

        private SongsDao songsDao;
        private int operation;

        DBAsyncTask(SongsDao songsDao, int operation) {
            this.songsDao = songsDao;
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
