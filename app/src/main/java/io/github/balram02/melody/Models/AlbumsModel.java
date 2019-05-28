package io.github.balram02.melody.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "albums_table")
public class AlbumsModel {

    @PrimaryKey
    private String album;

    public AlbumsModel(String album) {
        this.album = album;
    }

    public String getAlbum() {
        return album;
    }

}
