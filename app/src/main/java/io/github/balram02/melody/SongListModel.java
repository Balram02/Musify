package io.github.balram02.melody;

public class SongListModel {

    private String name;
    private String artist;
    private String path;

    public SongListModel(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public SongListModel(String name, String artist, String path) {
        this.name = name;
        this.artist = artist;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

}
