package io.github.balram02.musify.listeners;

import io.github.balram02.musify.models.SongsModel;

public interface MusicPlayerServiceListener {
    void onUpdateService(SongsModel currentModel);

    void onPlayFromFavorites(SongsModel currentModel, boolean shuffleFav);
}
