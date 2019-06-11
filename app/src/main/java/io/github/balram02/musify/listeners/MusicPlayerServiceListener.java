package io.github.balram02.musify.listeners;

import androidx.lifecycle.AndroidViewModel;

import io.github.balram02.musify.models.SongsModel;

public interface MusicPlayerServiceListener {
    void onUpdateService(SongsModel currentModel, AndroidViewModel mViewModel);
}
