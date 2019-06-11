package io.github.balram02.musify.database;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import io.github.balram02.musify.models.SongsModel;

@Database(entities = {SongsModel.class}, version = 1)
public abstract class SongsDatabase extends RoomDatabase {

    private static SongsDatabase instance;

    public abstract SongsDao songDao();

    public static synchronized SongsDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), SongsDatabase.class, "songs_database")
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            new PopulateAsyncTask(instance).execute(context);
                        }
                    }).build();
        }
        return instance;
    }

    private static class PopulateAsyncTask extends AsyncTask<Context, Void, Void> {

        private SongsDao songsDao;

        PopulateAsyncTask(SongsDatabase database) {
            songsDao = database.songDao();
        }

        @Override
        protected Void doInBackground(Context... contexts) {

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            Cursor cursor = contexts[0].getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {

                int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int genre = cursor.getColumnIndex(MediaStore.Audio.Genres.NAME);
                int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                SongsModel songsModel;
                do {
                    String songTitle = cursor.getString(title);
                    String songAlbum = cursor.getString(album);
                    String songArtist = cursor.getString(artist);
//                    String songGenre = cursor.getString(genre);
                    String songPath = cursor.getString(path);
                    long songDuration = cursor.getLong(duration);
                    songsModel = new SongsModel(songTitle, songAlbum, songArtist, "", songPath, songDuration);

                    songsDao.insert(songsModel);
                } while (cursor.moveToNext());
            }
            if (cursor != null)
                cursor.close();
            return null;
        }
    }
}
