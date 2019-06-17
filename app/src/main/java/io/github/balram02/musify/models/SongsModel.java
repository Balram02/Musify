package io.github.balram02.musify.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "songs_table")
public class SongsModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String album;
    @ColumnInfo(name = "album_id")
    private long albumId;
    private String artist;
    private String genre;
    private String path;
    private long duration;
    @ColumnInfo(name = "is_favorite")
    private boolean favorite = false;
    @ColumnInfo(name = "accessed_timestamp")
    private Long lastAccessedTimestamp = 0L;

    public SongsModel(String title, String album, long albumId, String artist, String genre, String path, long duration) {
        this.title = title;
        this.album = album;
        this.albumId = albumId;
        this.artist = artist;
        this.genre = genre;
        this.path = path;
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setLastAccessedTimestamp(long lastAccessedTimestamp) {
        this.lastAccessedTimestamp = lastAccessedTimestamp;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
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

    public long getAlbumId() {
        return albumId;
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

    public boolean isFavorite() {
        return favorite;
    }

    public Long getLastAccessedTimestamp() {
        return lastAccessedTimestamp;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SongsModel)
            return this.getPath().equals(((SongsModel) obj).getPath());
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        return this.title + " , " + this.album + " , " + this.artist;
    }
}
