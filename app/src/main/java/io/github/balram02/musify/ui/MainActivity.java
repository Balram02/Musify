package io.github.balram02.musify.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.ViewModels.AllSongsViewModel;
import io.github.balram02.musify.constants.Constants;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, MusicPlayerServiceListener {

    public final String TAG = Constants.TAG;

    private FragmentManager fragmentManager;
    public static BottomNavigationView navigationView;

    private boolean isBound;

    public MusicPlayerService musicPlayerService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            isBound = true;
            musicPlayerService = ((MusicPlayerService.PlayerServiceBinder) iBinder).getBoundedService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        navigationView = findViewById(R.id.bottom_nav_view);
        navigationView.setOnNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        askRequiredPermissions();
    }

    private void askRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "asking permissions... ");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.STORAGE_PERMISSION_REQUEST_CODE);
            } else {
                setFragment(new AllSongsFragment());
            }
        } else {
            setFragment(new AllSongsFragment());
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        startService(serviceIntent);
    }

    public void setFragment(Fragment fragment) {
        new Thread(() -> runOnUiThread(() -> {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        })).start();
    }

    @Override
    public void onUpdateService(SongsModel model, AndroidViewModel mViewModel) {

        if (mViewModel instanceof AllSongsViewModel) {
            musicPlayerService.setSongDetails(model.getTitle(), model.getArtist(), model.getPath());
        }
        startService();

/*        if (musicPlayerService.isPlaying()) {
            musicPlayerService.pause();
        } else {

            musicPlayerService.start();

            if (!musicPlayerService.isPaused()) {
                musicPlayerService.setSongDetails(model.getTitle(), model.getArtist(), model.getPath());

                if (mViewModel instanceof AllSongsViewModel)
                    musicPlayerService.setSongsQueueList(((AllSongsViewModel) mViewModel).getSongsQueue().getValue());
            }
        }*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.music:
                if (!(fragmentManager.findFragmentById(R.id.fragment_container) instanceof AllSongsFragment))
                    setFragment(new AllSongsFragment());
                break;
            case R.id.library:
                if (!(fragmentManager.findFragmentById(R.id.fragment_container) instanceof LibraryFragment))
                    setFragment(new LibraryFragment());
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent playerServiceIntent = new Intent(this, MusicPlayerService.class);
        bindService(playerServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(mServiceConnection);
            isBound = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setFragment(new AllSongsFragment());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission denied")
                    .setMessage("Storage permissions are needed for this app to work properly.\nApp will close if canceled")
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        finish();
                    }).setPositiveButton("Ok", (dialog, which) -> {
                askRequiredPermissions();
            });
            builder.show();
        }
    }
}
