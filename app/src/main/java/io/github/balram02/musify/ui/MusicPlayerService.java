package io.github.balram02.musify.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.MediaController;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.List;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaController.MediaPlayerControl {

    public static final String CHANNEL_ID = "512";
    private boolean wasPaused;
    private boolean isRunning;

    private MediaPlayer player;
    private String TAG = Constants.TAG;
    private Notification notification;
    private NotificationManager manager;

    private String title;
    private String artist;
    private String path;
    private int lastSeekTo;

    public void setSongsQueueList(List<SongsModel> songsQueueList) {
        this.songsQueueList = songsQueueList;
    }

    private final Binder mBinder = new PlayerServiceBinder();
    private List<SongsModel> songsQueueList;

    public class PlayerServiceBinder extends Binder {
        public MusicPlayerService getBoundedService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        isRunning = true;

        player = new MediaPlayer();
        player.setLooping(false);
        player.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        createNotificationChannel();
        createNotification(false);
        startNotification();

/*        if (!isRunning) {

        }*/

//        startForeground(2, notification);

        return START_NOT_STICKY;
    }

    private void startNotification() {
        startForeground(2, notification);
    }

    private void createNotification(boolean forPause) {

        RemoteViews notificationCollapsed = new RemoteViews(getPackageName(), R.layout.notification_collapsed_layout);
        RemoteViews notificationExpanded = new RemoteViews(getPackageName(), R.layout.notification_expanded_layout);

        notificationCollapsed.setTextViewText(R.id.notification_song_name, title);
        notificationExpanded.setTextViewText(R.id.notification_song_name, title);

        notificationCollapsed.setTextViewText(R.id.notification_song_artist_name, artist);
        notificationExpanded.setTextViewText(R.id.notification_song_artist_name, artist);

        notificationCollapsed.setImageViewResource(R.id.notification_previous_icon, R.drawable.previous_icon_black_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_previous_icon, R.drawable.previous_icon_black_24dp);

        notificationCollapsed.setImageViewResource(R.id.notification_play_pause_icon,
                forPause ? R.drawable.pause_icon_black_24dp : R.drawable.play_icon_black_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_play_pause_icon,
                forPause ? R.drawable.pause_icon_black_24dp : R.drawable.play_icon_black_24dp);

        notificationCollapsed.setImageViewResource(R.id.notification_next_icon, R.drawable.next_icon_black_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_next_icon, R.drawable.next_icon_black_24dp);

        notificationCollapsed.setImageViewResource(R.id.notification_close_icon, R.drawable.ic_close_white_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_close_icon, R.drawable.ic_close_white_24dp);

        Intent previousIntent = new Intent(this, MusicPlayerService.class);
        previousIntent.setAction(Constants.ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, MusicPlayerService.class);
        playIntent.setAction(Constants.ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent pauseIntent = new Intent(this, MusicPlayerService.class);
        pauseIntent.setAction(Constants.ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Intent nextIntent = new Intent(this, MusicPlayerService.class);
        nextIntent.setAction(Constants.ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
                .setCustomContentView(notificationCollapsed)
                .setCustomBigContentView(notificationExpanded)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "description", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Music notification");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void setSongDetails(String title, String artist, String path) {
        this.title = title;
        this.artist = artist;
        this.path = path;
    }

    public void startPlayer() {

//        if ()
    }

    @Override
    public void start() {
        player.start();
    }

    public boolean wasPaused() {
        return wasPaused;
    }

    @Override
    public void pause() {
        player.pause();
        wasPaused = true;
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
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
//        isServiceRunning = false;
        stopForeground(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: ");
        onDestroy();
    }

}
