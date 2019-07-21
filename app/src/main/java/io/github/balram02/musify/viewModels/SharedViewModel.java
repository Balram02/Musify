package io.github.balram02.musify.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.repositories.SongsRepository;

public class SharedViewModel extends AndroidViewModel {

    private SongsRepository repository;
    private LiveData<List<SongsModel>> songs;
    private LiveData<List<SongsModel>> recentSongs;
    private LiveData<List<SongsModel>> songsByAlbums;
    private LiveData<List<SongsModel>> songsByArtist;


    public SharedViewModel(@NonNull Application application) {
        super(application);
        repository = new SongsRepository(application);
        songs = repository.getAllSongs();
        recentSongs = repository.getRecentlyPlayedSongs();
        songsByAlbums = repository.getAlbums();
        songsByArtist = repository.getArtist();
    }

    public void update(SongsModel songsModel) {
        repository.update(songsModel);
    }

    public void delete(SongsModel songsModel) {
        repository.delete(songsModel);
    }

    public LiveData<List<SongsModel>> getAllSongs() {
        return songs;
    }

    public List<SongsModel> getAllSongsQueue() {
        return repository.getAllSongsQueue();
    }

    public List<SongsModel> getShuffleSongsQueue() {
        return repository.getShuffleSongsQueue();
    }

    public LiveData<Boolean> isFavorite(String path) {
        return repository.isFavorite(path);
    }

    public LiveData<List<SongsModel>> getFavoriteSong() {
        return repository.getFavoriteSong();
    }

    public List<SongsModel> getFavoritesQueueList() {
        return repository.getFavoritesQueueList();
    }

    public List<SongsModel> getFavoritesShuffleQueueList() {
        return repository.getFavoritesShuffleQueueList();
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
        return repository.getSongsByAlbums(albumName);
    }

    public LiveData<List<SongsModel>> getSongsByArtist(String artistName) {
        return repository.getSongsByArtist(artistName);
    }

    public List<SongsModel> getSearchQueryResults(String queryText) {
        return repository.getSearchQueryResults(queryText);
    }

}
