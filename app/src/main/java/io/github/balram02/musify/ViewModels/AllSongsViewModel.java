package io.github.balram02.musify.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.Repositories.SongsRepository;

public class AllSongsViewModel extends AndroidViewModel {

    private SongsRepository repository;
    private LiveData<List<SongsModel>> songs;
    private LiveData<List<SongsModel>> songsQueue;
    private LiveData<SongsModel> firstSongModel;

    public AllSongsViewModel(@NonNull Application application) {
        super(application);
        repository = new SongsRepository(application);
        songs = repository.getAllSongs();
        songsQueue = repository.getSongsQueue();
        firstSongModel = repository.getFirstSong();
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

    public LiveData<List<SongsModel>> getSongsQueue() {
        return songsQueue;
    }

    public LiveData<SongsModel> getFirstSong() {
        return firstSongModel;
    }

}
