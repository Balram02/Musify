package io.github.balram02.musify.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import io.github.balram02.musify.models.SongsModel;

import static io.github.balram02.musify.constants.Constants.PREFERENCES_REPEAT_STATE_NONE;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_SHUFFLE_STATE_NO;

public class Preferences {

    private static final String SONGS_DETAILS = "general_preferences";
    private static final String DEFAULT_SETTINGS = "other_preferences";

    private static SharedPreferences sharedPreferences;

    public static class DefaultSettings {

        /**************save operations**************/

        // save when app is launched first time
        synchronized public static void setFirstLaunch(Context context) {
            sharedPreferences = context.getSharedPreferences(DEFAULT_SETTINGS, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("is_first_launch", false).apply();
        }

        // save repeat state
        synchronized public static void setRepeatState(Context context, int state) {
            context.getSharedPreferences(SONGS_DETAILS, Context.MODE_PRIVATE).edit()
                    .putInt("repeat_state", state).apply();
        }

        // save shuffle state
        synchronized public static void setShuffleState(Context context, boolean state) {
            context.getSharedPreferences(SONGS_DETAILS, Context.MODE_PRIVATE).edit()
                    .putBoolean("shuffle_state", state).apply();
        }


        /**************retrieving operations**************/

        // check if app is launched first time
        synchronized public static boolean isFirstLaunch(Context context) {
            return context.getSharedPreferences(DEFAULT_SETTINGS, Context.MODE_PRIVATE)
                    .getBoolean("is_first_launch", true);
        }

        // retrieve repeat state
        synchronized public static int getRepeatState(Context context) {
            return context.getSharedPreferences(SONGS_DETAILS, Context.MODE_PRIVATE)
                    .getInt("repeat_state", PREFERENCES_REPEAT_STATE_NONE);
        }

        // retrieve shuffle state
        synchronized public static boolean getShuffleState(Context context) {
            return context.getSharedPreferences(SONGS_DETAILS, Context.MODE_PRIVATE)
                    .getBoolean("shuffle_state", PREFERENCES_SHUFFLE_STATE_NO);
        }

    }

    public static class SongDetails {

        /**************save operations**************/

        // save last song details
        synchronized public static void setLastSongDetails(Context context, SongsModel model) {
            sharedPreferences = context.getSharedPreferences(SONGS_DETAILS, Context.MODE_PRIVATE);
            sharedPreferences.edit()
                    .putString("last_song_model", new Gson().toJson(model)).apply();
        }

        // save song's last position
        synchronized public static void setLastSongCurrentPosition(Context context, int position) {
            context.getSharedPreferences(SONGS_DETAILS, Context.MODE_PRIVATE).edit()
                    .putInt("last_song_current_position", position).apply();
        }

        /**************retrieving operations**************/

        // retrieve last song details
        synchronized public static SongsModel getLastSongDetails(Context context) {
            return new Gson().fromJson(context.getSharedPreferences(SONGS_DETAILS, Context.MODE_PRIVATE)
                    .getString("last_song_model", null), SongsModel.class);
        }

        // retrieve song's last position
        synchronized public static int getLastSongCurrentPosition(Context context) {
            return context.getSharedPreferences(SONGS_DETAILS, Context.MODE_PRIVATE)
                    .getInt("last_song_current_position", 0);
        }

    }

}
