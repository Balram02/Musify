package io.github.balram02.musify.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs_table")
public class SongsModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String album;
    private String artist;
    private String genre;
    private String path;
    private long duration;

    public SongsModel(String title, String album, String artist, String genre, String path, long duration) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.genre = genre;
        this.path = path;
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getPath() {
        return path;
    }

    public long getDuration() {
        return duration;
    }

    @NonNull
    @Override
    public String toString() {
        return this.title + " , " + this.album + " , " + this.artist;
    }
}
