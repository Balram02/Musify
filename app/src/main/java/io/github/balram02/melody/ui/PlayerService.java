package io.github.balram02.melody.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import io.github.balram02.melody.R;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {

    public static final String CHANNEL_ID = "512";

    private MediaPlayer player;
    private String TAG = "PlayerService";
    private Notification notification;
    private NotificationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            String path = intent.getStringExtra("song_path");
            Log.d(TAG, "Path = " + path);
            player = new MediaPlayer();
            player.setDataSource(path);
            player.prepare();
            player.setLooping(false);
            player.start();
            player.setOnCompletionListener(this);

//            createNotificationBuilder();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "description", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("other description");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

//            createNotificationChannel();
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(intent.getStringExtra("song_name"))
                    .setContentText(intent.getStringExtra("song_artist"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .build();

            startForeground(2, notification);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error " + e, Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.toString());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.stop();
            player.release();
        }
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onDestroy();
    }
}
