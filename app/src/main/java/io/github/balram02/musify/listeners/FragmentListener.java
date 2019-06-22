package io.github.balram02.musify.listeners;

public interface FragmentListener {

    String ALBUM_FRAGMENT = "album_fragment";
    String ARTIST_FRAGMENT = "artist_fragment";

    void setCommonFragmentType(String fragmentType);
}
