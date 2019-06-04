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
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.List;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener {

    public static final String CHANNEL_ID = "512";
    public static final int NOTIFICATION_ID = 2;
    private boolean wasPaused;
    private boolean serviceCreated;

    private MediaPlayer player;
    private String TAG = Constants.TAG;
    private Notification notification;
    private NotificationManager manager;

    private SongsModel model;
    private SongsModel previousModel;
    private SongsModel nextModel;
    private String title;
    private String artist;
    private String path;
    private int lastSeekTo;

    private RemoteViews notificationCollapsed;
    private RemoteViews notificationExpanded;

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

        serviceCreated = true;

        player = new MediaPlayer();
//        player.setLooping(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent.getAction());

        if (intent.getAction() == null) {
            createNotificationChannel();
            createNotification(true);
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


    private void createNotification(boolean pauseButton) {

        notificationCollapsed = new RemoteViews(getPackageName(), R.layout.notification_collapsed_layout);
        notificationExpanded = new RemoteViews(getPackageName(), R.layout.notification_expanded_layout);

        notificationCollapsed.setTextViewText(R.id.notification_song_name, title);
        notificationExpanded.setTextViewText(R.id.notification_song_name, title);

        notificationCollapsed.setTextViewText(R.id.notification_song_artist_name, artist);
        notificationExpanded.setTextViewText(R.id.notification_song_artist_name, artist);

        notificationCollapsed.setImageViewResource(R.id.notification_previous_icon, R.drawable.previous_icon_black_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_previous_icon, R.drawable.previous_icon_black_24dp);

        notificationCollapsed.setImageViewResource(R.id.notification_play_pause_icon,
                pauseButton ? R.drawable.pause_icon_black_24dp : R.drawable.play_icon_black_24dp);
        notificationExpanded.setImageViewResource(R.id.notification_play_pause_icon,
                pauseButton ? R.drawable.pause_icon_black_24dp : R.drawable.play_icon_black_24dp);

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

        Intent closeIntent = new Intent(this, MusicPlayerService.class);
        closeIntent.setAction(Constants.ACTION_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        notificationCollapsed.setOnClickPendingIntent(R.id.notification_previous_icon, previousPendingIntent);
        notificationCollapsed.setOnClickPendingIntent(R.id.notification_play_pause_icon,
                pauseButton ? pausePendingIntent : playPendingIntent);
        notificationCollapsed.setOnClickPendingIntent(R.id.notification_next_icon, nextPendingIntent);
        notificationCollapsed.setOnClickPendingIntent(R.id.notification_close_icon, closePendingIntent);

        notificationExpanded.setOnClickPendingIntent(R.id.notification_previous_icon, previousPendingIntent);
        notificationExpanded.setOnClickPendingIntent(R.id.notification_play_pause_icon,
                pauseButton ? pausePendingIntent : playPendingIntent);
        notificationExpanded.setOnClickPendingIntent(R.id.notification_next_icon, nextPendingIntent);
        notificationExpanded.setOnClickPendingIntent(R.id.notification_close_icon, closePendingIntent);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
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

    //    public void setSongDetails(String title, String artist, String path) {
//            this.title = title;
//        this.artist = artist;
//        this.path = path;
    public void setSongDetails(SongsModel previousModel, SongsModel model, SongsModel nextModel) {
        this.previousModel = previousModel;
        this.model = model;
        this.nextModel = nextModel;

        this.title = model.getTitle();
        this.artist = model.getArtist();
        this.path = model.getPath();

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
            player.setOnCompletionListener(this);

            Log.d(TAG, "startPlayer: " + path + player.getDuration());
        } catch (IOException io) {
            Log.d(TAG, "IOException caught" + io);

        } catch (Exception e) {
            Log.d(TAG, "Other Exception caught" + e + path);
        }
    }

    private void setDataSourceAndPrepare(String sourcePath) throws IOException {
        player.setDataSource(sourcePath);
        player.prepare();
    }

    private void start() {
        player.start();
    }

    private boolean wasPaused() {
        return wasPaused;
    }

    private void pause() {
        player.pause();
        wasPaused = true;
    }

    private int getDuration() {
        return player.getDuration();
    }

    private int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    private void seekTo(int pos) {
        player.seekTo(pos);
    }

    private boolean isPlaying() {
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
