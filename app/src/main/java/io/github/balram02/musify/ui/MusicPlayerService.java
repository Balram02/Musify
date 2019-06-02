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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.List;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaController.MediaPlayerControl {

    public static final String CHANNEL_ID = "512";
    private boolean wasPaused;

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

    public MusicPlayerService() {
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        try {

//            if (!isServiceRunning) {
            player = new MediaPlayer();
            player.setLooping(false);
            player.setOnCompletionListener(this);

//            createNotificationChannel();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "description", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("other description");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

            RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_layout);
//                notificationView.addView();

            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationView)
                    .build();

            Intent serviceIntent = new Intent(this, MusicPlayerService.class);
//                serviceIntent.
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);

//                notificationView.setOnClickPendingIntent(R.id.play_pause, pendingIntent);

            startForeground(2, notification);
//            isServiceRunning = true;
//            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error " + e, Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.toString());
        }
        return START_NOT_STICKY;
    }


    public void setSongDetails(String title, String artist, String path) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        try {
            player.setDataSource(path);
            player.prepare();
        } catch (Exception e) {
            Log.d(TAG, "setSongDetails: " + path);
            Log.d(TAG, "setSongDetails: " + e);
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
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
