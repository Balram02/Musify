package io.github.balram02.musify.listeners;

import java.util.List;

import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.viewModels.SharedViewModel;

public interface MusicPlayerServiceListener {
    void onUpdateService(List<SongsModel> songsModels,SongsModel currentModel, SharedViewModel mViewModel);
}
