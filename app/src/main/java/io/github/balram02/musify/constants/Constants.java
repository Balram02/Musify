package io.github.balram02.musify.constants;

public class Constants {

    public static final String PREFERENCES_DETAILS = "updating_list";

    public static final String REFRESH_SONG_LIST = "refresh_song_list";

    public static final int STORAGE_PERMISSION_REQUEST_CODE = 101;

    public static final String TAG = "MyTag";

    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_PREVIOUS = "previous";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_CLOSE = "close";

    public static String millisecondsToMinutesAndSeconds(long milliseconds) {

        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;

        return (String.valueOf(minutes).length() >= 2 ? minutes : "0" + minutes) + ":" +
                (String.valueOf(seconds).length() >= 2 ? seconds : "0" + seconds);
    }

}
