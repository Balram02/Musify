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

    private List<SongsModel> songsQueue;
    private List<SongsModel> allSongsQueue;
    private List<SongsModel> favSongsQueueList;
    private List<SongsModel> favSongsShuffleQueueList;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        repository = new SongsRepository(application);
        songs = repository.getAllSongs();
        songsQueue = repository.getShuffleSongsQueue();
        allSongsQueue = repository.getAllSongsQueue();
        recentSongs = repository.getRecentlyPlayedSongs();
        songsByAlbums = repository.getAlbums();
        songsByArtist = repository.getArtist();
        favSongsQueueList = repository.getFavoritesQueueList();
        favSongsShuffleQueueList = repository.getFavoritesShuffleQueueList();
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
        return allSongsQueue;
    }

    public List<SongsModel> getShuffleSongsQueue() {
        return songsQueue;
    }

    public LiveData<Boolean> isFavorite(int id) {
        return repository.isFavorite(id);
    }

    public LiveData<List<SongsModel>> getFavoriteSong() {
        return repository.getFavoriteSong();
    }

    public List<SongsModel> getFavoritesQueueList() {
        return favSongsQueueList;
    }

    public List<SongsModel> getFavoritesShuffleQueueList() {
        return favSongsShuffleQueueList;
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
