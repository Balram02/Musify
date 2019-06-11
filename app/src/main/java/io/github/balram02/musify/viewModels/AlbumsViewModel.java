package io.github.balram02.musify.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.github.balram02.musify.models.AlbumsModel;
import io.github.balram02.musify.repositories.AlbumsRepository;

public class AlbumsViewModel extends AndroidViewModel {

    private AlbumsRepository repository;
    private LiveData<List<AlbumsModel>> songsByAlbum;

    public AlbumsViewModel(@NonNull Application application) {
        super(application);
        repository = new AlbumsRepository(application);
        songsByAlbum = repository.getAllSongsByAlbum();
    }

    public LiveData<List<AlbumsModel>> getAllSongsByAlbum() {
        return songsByAlbum;
    }
}
