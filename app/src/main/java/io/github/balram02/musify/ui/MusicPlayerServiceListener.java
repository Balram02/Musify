package io.github.balram02.musify.ui;

import androidx.lifecycle.AndroidViewModel;

import io.github.balram02.musify.Models.SongsModel;

public interface MusicPlayerServiceListener {
    void onUpdateService(SongsModel songsModel, AndroidViewModel mViewModel);
}