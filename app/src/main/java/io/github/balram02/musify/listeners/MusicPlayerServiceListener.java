package io.github.balram02.musify.listeners;

import androidx.lifecycle.AndroidViewModel;

import io.github.balram02.musify.Models.SongsModel;

public interface MusicPlayerServiceListener {
    void onUpdateService(SongsModel previousModel, SongsModel currentModel, SongsModel nextModel, AndroidViewModel mViewModel);
}
