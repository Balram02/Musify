package io.github.balram02.musify.utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;

public class Constants {

    public static final int STORAGE_PERMISSION_REQUEST_CODE = 101;

    public static final String TAG = "MyTag";

    //    Service Intent actions
    public static final String INTENT_ACTION_NEW_SONG = "intent_action_new_song";
    public static final String INTENT_ACTION_PAUSE = "intent_action_pause";
    public static final String INTENT_ACTION_PLAY = "intent_action_play";
    public static final String INTENT_ACTION_PREVIOUS = "intent_action_previous";
    public static final String INTENT_ACTION_NEXT = "intent_action_next";
    public static final String INTENT_ACTION_CLOSE = "intent_action_close";

    //    Repeat states
    public static final int PREFERENCES_REPEAT_STATE_NONE = 101;
    public static final int PREFERENCES_REPEAT_STATE_ALL = 102;
    public static final int PREFERENCES_REPEAT_STATE_ONE = 100;

    //    Shuffle states
    public static final boolean PREFERENCES_SHUFFLE_STATE_YES = true;
    public static final boolean PREFERENCES_SHUFFLE_STATE_NO = false;

    public static final String PREFERENCES_ACTIVITY_STATE = "isForeground";

    public static String convertMilliseconds(long milliseconds) {

        String minutes = (milliseconds / 1000) / 60 + "";
        String seconds = (milliseconds / 1000) % 60 + "";

        return (minutes.length() >= 2 ? minutes : "0" + minutes) + ":" +
                (seconds.length() >= 2 ? seconds : "0" + seconds);
    }

    public static Uri getAlbumArtUri(Long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    public static Bitmap getAlbumArt(Context context, Long album_id) {

        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            try(ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");) {
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "getAlbumArt: " + e);
        }
        return bm;
    }

}
