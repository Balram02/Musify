package io.github.balram02.melody.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.balram02.melody.Models.SongsModel;
import io.github.balram02.melody.Repositories.SongsRepository;

public class AllSongsViewModel extends AndroidViewModel {

    private SongsRepository repository;
    private LiveData<List<SongsModel>> songs;

    public AllSongsViewModel(@NonNull Application application) {
        super(application);
        repository = new SongsRepository(application);
        songs = repository.getAllSongs();
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

}
