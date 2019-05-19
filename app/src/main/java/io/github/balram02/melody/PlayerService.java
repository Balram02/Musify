package io.github.balram02.melody;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PlayerService extends Service {

    private MediaPlayer player;
    private String TAG = "PlayerService";

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
            player.setLooping(true);
            player.start();

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
