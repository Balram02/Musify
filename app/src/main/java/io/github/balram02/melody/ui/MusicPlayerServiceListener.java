package io.github.balram02.melody.ui;

import androidx.lifecycle.AndroidViewModel;

import io.github.balram02.melody.Models.SongsModel;

public interface MusicPlayerServiceListener {
    void onUpdateService(SongsModel songsModel, AndroidViewModel mViewModel);
}
