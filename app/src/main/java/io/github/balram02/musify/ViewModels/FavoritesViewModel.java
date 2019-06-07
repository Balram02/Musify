package io.github.balram02.musify.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.Repositories.SongsRepository;

public class FavoritesViewModel extends AndroidViewModel {

    private SongsRepository repository;
    private LiveData<List<SongsModel>> favorites;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repository = new SongsRepository(application);
        favorites = repository.getFavoriteSong();
    }

    public void update(SongsModel songsModel) {
        repository.update(songsModel);
    }

    public void delete(SongsModel songsModel) {
        repository.delete(songsModel);
    }

    public LiveData<List<SongsModel>> getFavoriteSong() {
        return favorites;
    }

}
