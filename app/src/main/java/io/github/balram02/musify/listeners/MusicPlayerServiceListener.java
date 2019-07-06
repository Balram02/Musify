package io.github.balram02.musify.listeners;

import java.util.List;

import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.viewModels.SharedViewModel;

public interface MusicPlayerServiceListener {
    void onUpdateService(SongsModel currentModel);
    void onPlayFromFavorites(SongsModel currentModel,boolean shuffleFav);
}
