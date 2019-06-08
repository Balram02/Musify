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

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.List;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.ui.MainActivity;
import io.github.balram02.musify.utils.Preferences;

import static io.github.balram02.musify.constants.Constants.BROADCAST_ACTION_PAUSE;
import static io.github.balram02.musify.constants.Constants.BROADCAST_ACTION_PLAY;
import static io.github.balram02.musify.constants.Constants.TAG;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public static final String CHANNEL_ID = "512";
    public static final int NOTIFICATION_ID = 2;
    private boolean wasPaused;

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
    private SongsModel previousModel;
    private SongsModel nextModel;
    private String title;
    private String artist;
    private String path;
    private int lastSeekTo;

    private RemoteViews notificationCollapsed;
    private RemoteViews notificationExpanded;
    private AudioManager mAudioManager;

    private final Binder mBinder = new PlayerServiceBinder();
    private List<SongsModel> songsQueueList;
    private Intent intent;

    private AudioManager audioManager;

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                player.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                pause();
                break;
        }
    }

    public String getArtistName() {
        return artist;
    }

    public class PlayerServiceBinder extends Binder {
        public MusicPlayerService getBoundedService() {
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
        player.setOnCompletionListener(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public void setSongsQueueList(List<SongsModel> songsQueueList) {
        this.songsQueueList = songsQueueList;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent.getAction());

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (intent.getAction() == null) {
            createNotificationChannel();
//            createNotification(true);
            startPlayer();
        } else if (intent.getAction().equals(Constants.ACTION_PAUSE)) {
            pause();
            createNotification(false);
        } else if (intent.getAction().equals(Constants.ACTION_PLAY)) {
            createNotification(true);
            startPlayer();
        } else if (intent.getAction().equals(Constants.ACTION_CLOSE)) {
            onDestroy();
        }

        return START_NOT_STICKY;
    }


    public void createNotification(boolean pauseButton) {

        if (notificationCollapsed == null)
            notificationCollapsed = new RemoteViews(getPackageName(), R.layout.notification_collapsed_layout);
        if (notificationExpanded == null)
            notificationExpanded = new RemoteViews(getPackageName(), R.layout.notification_expanded_layout);

        notificationCollapsed.setTextViewText(R.id.notification_song_name, title);
        notificationExpanded.setTextViewText(R.id.notification_song_name, title);

        notificationCollapsed.setTextViewText(R.id.notification_song_artist_name, artist);
        notificationExpanded.setTextViewText(R.id.notification_song_artist_name, artist);

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
            previousIntent.setAction(Constants.ACTION_PREVIOUS);
            if (previousPendingIntent == null)
                previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, 0);
        }
        if (playIntent == null) {
            playIntent = new Intent(this, MusicPlayerService.class);
            playIntent.setAction(Constants.ACTION_PLAY);
            if (playPendingIntent == null)
                playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);
        }

        if (pauseIntent == null) {
            pauseIntent = new Intent(this, MusicPlayerService.class);
            pauseIntent.setAction(Constants.ACTION_PAUSE);
            if (pausePendingIntent == null)
                pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        }

        if (nextIntent == null) {
            nextIntent = new Intent(this, MusicPlayerService.class);
            nextIntent.setAction(Constants.ACTION_NEXT);
            if (nextPendingIntent == null)
                nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0);
        }

        if (closeIntent == null) {
            closeIntent = new Intent(this, MusicPlayerService.class);
            closeIntent.setAction(Constants.ACTION_CLOSE);
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
                .setCustomContentView(notificationCollapsed)
                .setCustomBigContentView(notificationExpanded)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        if (!pauseButton)
            stopForeground(false);
    }

    private void createNotificationChannel() {

        NotificationManager manager;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "description", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Music notification");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setSound(null, null);
            channel.enableVibration(false);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void setSongDetails(SongsModel previousModel, SongsModel model, SongsModel nextModel) {
        this.previousModel = previousModel;
        this.model = model;
        this.nextModel = nextModel;

        this.title = model.getTitle();
        this.artist = model.getArtist();
        this.path = model.getPath();

    }

    private boolean sendBroadcastToLocal(String action) {
        intent = new Intent();
        intent.setAction(action);
        intent.putExtra("song_details", model);
        return LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void startPlayer() {
        try {
            if (wasPaused()) {
                start();
                wasPaused = false;
            } else {
                player.reset();
                setDataSourceAndPrepare(path);
            }

            start();

            Log.d(TAG, "startPlayer: " + path + player.getDuration());
        } catch (IOException io) {
            Log.d(TAG, "IOException caught" + io);

        } catch (Exception e) {
            Log.d(TAG, "Other Exception caught" + e + path);
        }

//        player.ch

    }

    private void setDataSourceAndPrepare(String sourcePath) throws IOException {
        player.setDataSource(sourcePath);
        player.prepare();
    }

    private void start() {

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            sendBroadcastToLocal(BROADCAST_ACTION_PLAY);
            player.start();

        }
    }

    private boolean wasPaused() {
        return wasPaused;
    }

    public void pause() {
        player.pause();
        sendBroadcastToLocal(BROADCAST_ACTION_PAUSE);
        Preferences.SongDetails.setLastSongDetails(this, model);
        Preferences.SongDetails.setLastSongCurrentPosition(this, getCurrentPosition());
        Preferences.SongDetails.setLastSongMaxDuration(this, getDuration());
        wasPaused = true;
    }

    public String getSongName() {
        return title;
    }

    public int getDuration() {
        Log.i(TAG, "getDuration: " + player.getDuration());
        return (player.getDuration() / 1000) % 60;
    }

    public int getCurrentPosition() {
        Log.i(TAG, "getCurrentPosition: " + player.getCurrentPosition());
        return (player.getCurrentPosition() / 1000);
    }

    public void seekTo(int pos) {
        player.seekTo(pos);
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

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (player != null) {
            player.stop();
            player.reset();
        }
        stopForeground(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: ");

        try {
//            onStartCommand(new Intent().setAction(null), 0, 0);
            player.reset();
            setDataSourceAndPrepare(nextModel.getPath());
            start();
        } catch (Exception e) {
            Log.d(TAG, "onCompletion: Exception caught " + e);
        }
    }
}
