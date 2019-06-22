package io.github.balram02.musify.constants;

import android.content.ContentUris;
import android.net.Uri;

public class Constants {

    public static final String PREFERENCES_DETAILS = "updating_list";

    public static final String REFRESH_SONG_LIST = "refresh_song_list";

    public static final int STORAGE_PERMISSION_REQUEST_CODE = 101;

    public static final String TAG = "MyTag";

    //    Service Intent actions
    public static final String INTENT_ACTION_NEW_SONG = "intent_action_new_song";
    public static final String INTENT_ACTION_PAUSE = "intent_action_pause";
    public static final String INTENT_ACTION_PLAY = "intent_action_play";
    public static final String INTENT_ACTION_PREVIOUS = "intent_action_previous";
    public static final String INTENT_ACTION_NEXT = "intent_action_next";
    public static final String INTENT_ACTION_CLOSE = "intent_action_close";

    //    Local Broadcast Actions
    public static final String BROADCAST_ACTION_PLAY = "broadcast_action_play";
    public static final String BROADCAST_ACTION_PAUSE = "broadcast_action_pause";

    //    Repeat states
    public static final int PREFERENCES_REPEAT_STATE_NONE = 0;
    public static final int PREFERENCES_REPEAT_STATE_ALL = -1;
    public static final int PREFERENCES_REPEAT_STATE_ONE = 1;

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

}
