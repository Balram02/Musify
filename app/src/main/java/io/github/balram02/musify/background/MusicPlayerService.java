package io.github.balram02.musify.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import io.github.balram02.musify.R;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.ui.MainActivity;
import io.github.balram02.musify.utils.Preferences;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.constants.Constants.BROADCAST_ACTION_PAUSE;
import static io.github.balram02.musify.constants.Constants.BROADCAST_ACTION_PLAY;
import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_CLOSE;
import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_NEW_SONG;
import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_NEXT;
import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_PAUSE;
import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_PLAY;
import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_PREVIOUS;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_REPEAT_STATE_NONE;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_REPEAT_STATE_ONE;
import static io.github.balram02.musify.constants.Constants.TAG;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public static final String CHANNEL_ID = "512";
    public static final int NOTIFICATION_ID = 512;
    //    private static boolean IS_NEW_SONG;
//    private boolean wasPaused;

    //    intents for notification
    private Intent activityIntent;
    private Intent previousIntent;
    private Intent playIntent;
    private Intent pauseIntent;
    private Intent nextIntent;
    private Intent closeIntent;

    //    pending intents for notification
    private PendingIntent activityPendingIntent;
    private PendingIntent previousPendingIntent;
    private PendingIntent playPendingIntent;
    private PendingIntent pausePendingIntent;
    private PendingIntent nextPendingIntent;
    private PendingIntent closePendingIntent;

    private MediaPlayer player;

    private SongsModel model;
    private SharedViewModel mViewModel;
    private List<SongsModel> songsQueueList;
    private int currentSongPosition;

    private RemoteViews notificationCollapsed;
    private RemoteViews notificationExpanded;
    private AudioManager mAudioManager;

    private final Binder mBinder = new PlayerServiceBinder();
    private Intent intent;
    private boolean setSeekTo = false;
    private NotificationManager manager;

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isPlaying()) {
                    pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if (!isPlaying()) {
                    start();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (isPlaying()) {
                    pause();
                }
                break;
        }
    }

    public String getArtistName() {
        return model.getArtist();
    }

    public class PlayerServiceBinder extends Binder {
        public MusicPlayerService getBoundService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        player = new MediaPlayer();
//        setLooping(true);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        player.setOnCompletionListener(this);

        player.setOnPreparedListener(mediaPlayer -> {
            if (setSeekTo) {
                player.seekTo(Preferences.SongDetails.getLastSongCurrentPosition(this));
                setSeekTo = false;
            }
            start();
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: " + intent.getAction());

        if (intent.getAction() != null && !intent.getAction().isEmpty()) {

            switch (intent.getAction()) {

                case INTENT_ACTION_NEW_SONG:
                    setDataSourceAndPrepare();
                    break;

                case INTENT_ACTION_PREVIOUS:
                    playPrevious();
                    break;

                case INTENT_ACTION_PAUSE:
                    pause();
                    break;

                case INTENT_ACTION_PLAY:
                    start();
                    break;

                case INTENT_ACTION_NEXT:
                    playNext();
                    break;

                case INTENT_ACTION_CLOSE:
                    pause();
                    stopForeground(true);
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    public void createNotification(boolean pauseButton) {

        if (notificationCollapsed == null)
            notificationCollapsed = new RemoteViews(getPackageName(), R.layout.notification_collapsed_layout);
        if (notificationExpanded == null)
            notificationExpanded = new RemoteViews(getPackageName(), R.layout.notification_expanded_layout);

        notificationCollapsed.setTextViewText(R.id.notification_song_name, model.getTitle());
        notificationExpanded.setTextViewText(R.id.notification_song_name, model.getTitle());

        notificationCollapsed.setTextViewText(R.id.notification_song_artist_name, model.getArtist());
        notificationExpanded.setTextViewText(R.id.notification_song_artist_name, model.getArtist());

        notificationCollapsed.setImageViewResource(R.id.notification_previous_icon, R.drawable.previous_icon_white_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_previous_icon, R.drawable.previous_icon_white_24dp);

        notificationCollapsed.setImageViewResource(R.id.notification_play_pause_icon,
                pauseButton ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_play_pause_icon,
                pauseButton ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp);

        notificationCollapsed.setImageViewResource(R.id.notification_next_icon, R.drawable.next_icon_white_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_next_icon, R.drawable.next_icon_white_24dp);

        notificationCollapsed.setImageViewResource(R.id.notification_close_icon, R.drawable.ic_close_white_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_close_icon, R.drawable.ic_close_white_24dp);

        if (activityIntent == null) {
            activityIntent = new Intent(this, MainActivity.class);
            if (activityPendingIntent == null)
                activityPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
        }

        if (previousIntent == null) {
            previousIntent = new Intent(this, MusicPlayerService.class);
            previousIntent.setAction(INTENT_ACTION_PREVIOUS);
            if (previousPendingIntent == null)
                previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, 0);
        }
        if (playIntent == null) {
            playIntent = new Intent(this, MusicPlayerService.class);
            playIntent.setAction(INTENT_ACTION_PLAY);
            if (playPendingIntent == null)
                playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);
        }

        if (pauseIntent == null) {
            pauseIntent = new Intent(this, MusicPlayerService.class);
            pauseIntent.setAction(INTENT_ACTION_PAUSE);
            if (pausePendingIntent == null)
                pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        }

        if (nextIntent == null) {
            nextIntent = new Intent(this, MusicPlayerService.class);
            nextIntent.setAction(INTENT_ACTION_NEXT);
            if (nextPendingIntent == null)
                nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0);
        }

        if (closeIntent == null) {
            closeIntent = new Intent(this, MusicPlayerService.class);
            closeIntent.setAction(INTENT_ACTION_CLOSE);
            if (closePendingIntent == null)
                closePendingIntent = PendingIntent.getService(this, 0, closeIntent, 0);
        }

        notificationCollapsed.setOnClickPendingIntent(R.id.notification_previous_icon, previousPendingIntent);
        notificationCollapsed.setOnClickPendingIntent(R.id.notification_play_pause_icon,
                pauseButton ? pausePendingIntent : playPendingIntent);
        notificationCollapsed.setOnClickPendingIntent(R.id.notification_next_icon, nextPendingIntent);
        notificationCollapsed.setOnClickPendingIntent(R.id.notification_close_icon, closePendingIntent);
        notificationCollapsed.setOnClickPendingIntent(R.id.root_layout, activityPendingIntent);

        notificationExpanded.setOnClickPendingIntent(R.id.notification_previous_icon, previousPendingIntent);
        notificationExpanded.setOnClickPendingIntent(R.id.notification_play_pause_icon,
                pauseButton ? pausePendingIntent : playPendingIntent);
        notificationExpanded.setOnClickPendingIntent(R.id.notification_next_icon, nextPendingIntent);
        notificationExpanded.setOnClickPendingIntent(R.id.notification_close_icon, closePendingIntent);
        notificationExpanded.setOnClickPendingIntent(R.id.root_layout, activityPendingIntent);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(pauseButton ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp)
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
                .setCustomContentView(notificationCollapsed)
                .setCustomBigContentView(notificationExpanded)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        if (!pauseButton)
            stopForeground(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "description", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Music notification");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        channel.setSound(null, null);
        channel.enableVibration(false);
        manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

    }

    public void setSongsQueueList(boolean isShuffled, List<SongsModel> songsQueueList) {

        this.songsQueueList = songsQueueList;

        if (isShuffled) {
            currentSongPosition = new Random().nextInt(songsQueueList.size());
            this.songsQueueList.add(currentSongPosition, this.model);
        } else
            currentSongPosition = this.songsQueueList.indexOf(model);

        currentSongPosition = new Random().nextInt(songsQueueList.size());
        this.songsQueueList.add(currentSongPosition, this.model != null ? model :
                Preferences.SongDetails.getLastSongDetails(this));
    }

    public void setSongDetails(List<SongsModel> songsQueueList, SongsModel model, SharedViewModel mViewModel) {
        this.model = model;
        this.mViewModel = mViewModel;
        this.songsQueueList = songsQueueList;

        if (Preferences.DefaultSettings.getShuffleState(this)) {
            currentSongPosition = new Random().nextInt(songsQueueList.size());
            this.songsQueueList.add(currentSongPosition, this.model);
        } else
            currentSongPosition = this.songsQueueList.indexOf(model);

        Log.d(TAG, "setSongDetails: position = " + currentSongPosition);
    }

    private void sendBroadcastToLocal(String action) {
        intent = new Intent();
        intent.setAction(action);
        intent.putExtra("song_details", model);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void setDataSourceAndPrepare() {

        try {
            reset();
            if (model != null) {
                player.setDataSource(model.getPath());
                player.prepare();
            } else
                return;

            setLooping(Preferences.DefaultSettings.getRepeatState(this) == PREFERENCES_REPEAT_STATE_ONE);

            Log.d(TAG, "setDataSourceAndPrepare: " + model.getPath());

        } catch (
                IOException io) {
            Log.e(TAG, model.getPath() + "IOException caught" + io);
            Toast.makeText(this, "Song not found on your storage device", Toast.LENGTH_SHORT).show();
        } catch (
                Exception e) {
            Log.e(TAG, "Other Exception caught" + e + model.getPath());
        }
    }

    private void start() {

        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            if (model == null) {
                model = Preferences.SongDetails.getLastSongDetails(this);
                setSeekTo = true;
                setDataSourceAndPrepare();
            } else {
                player.start();
                sendBroadcastToLocal(BROADCAST_ACTION_PLAY);
                createNotification(true);
            }
        } else {
            Toast.makeText(this, "cannot play music while other player is playing", Toast.LENGTH_SHORT).show();
        }
    }

    public void pause() {

        player.pause();
        createNotification(false);
        sendBroadcastToLocal(BROADCAST_ACTION_PAUSE);
        Preferences.SongDetails.setLastSongDetails(this, model);
        Preferences.SongDetails.setLastSongCurrentPosition(this, getCurrentPosition());

    }

    public void playNext() {

        if (songsQueueList.size() - 1 == currentSongPosition)
            currentSongPosition = 0;
        else
            currentSongPosition += 1;

        setSongModel(songsQueueList.get(currentSongPosition));
        setDataSourceAndPrepare();
    }

    public void playPrevious() {

        if (currentSongPosition == 0)
            currentSongPosition = songsQueueList.size() - 1;
        else
            currentSongPosition -= 1;

        setSongModel(songsQueueList.get(currentSongPosition));
        setDataSourceAndPrepare();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        stopForeground(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: " + isLooping());

        if (Preferences.DefaultSettings.getRepeatState(this) != PREFERENCES_REPEAT_STATE_NONE)
            playNext();
        else
            pause();
    }

    public void setLooping(boolean looping) {
        player.setLooping(looping);
    }

    public boolean isLooping() {
        return player.isLooping();
    }

    public void setSongModel(SongsModel model) {
        this.model = model;
    }

    public SongsModel getSongModel() {
        return model;
    }

    public int getSongId() {
        return model.getId();
    }

    public String getSongName() {
        return model.getTitle();
    }

    public int getDuration() {
        return (player.getDuration());
    }

    public int getCurrentPosition() {
        return (player.getCurrentPosition());
    }

    public void seekTo(int pos) {
        player.seekTo(pos);
    }

    public boolean isFavorite() {
        return model.isFavorite();
    }

    public void setFavorite(boolean favorite) {
        model.setFavorite(favorite);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    private int getBufferPercentage() {
        return 0;
    }

    private boolean canPause() {
        return true;
    }

    private boolean canSeekBackward() {
        return true;
    }

    private boolean canSeekForward() {
        return true;
    }

    private int getAudioSessionId() {
        return player.getAudioSessionId();
    }

    public void reset() {
        player.reset();
    }

    private void release() {
        player.release();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

}
