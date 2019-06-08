package io.github.balram02.musify.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.background.MusicPlayerService;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.listeners.MusicPlayerServiceListener;
import io.github.balram02.musify.utils.Preferences;

import static io.github.balram02.musify.constants.Constants.BROADCAST_ACTION_PAUSE;
import static io.github.balram02.musify.constants.Constants.BROADCAST_ACTION_PLAY;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_ACTIVITY_STATE;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, MusicPlayerServiceListener {

    public final String TAG = Constants.TAG;

    private LocalReceiver localReceiver;

    public TextView peekSongName;
    private ImageView peekFavorite;
    public ImageButton peekPlayPause;

    public TextView bottomSheetSongName;
    public TextView bottomSheetSongArtist;
    public ImageButton bottomSheetPlayPause;
    private ImageView bottomSheetFavorite;
    public SeekBar bottomSheetSeekbar;

    private FragmentManager fragmentManager;
    public static BottomNavigationView navigationView;

    private BottomSheetBehavior bottomSheet;
    private LinearLayout bottomSheetLayout;
    private LinearLayout bottomPeek;

    private boolean isBound;
    private Handler handler;

    public MusicPlayerService musicPlayerService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            isBound = true;
            musicPlayerService = ((MusicPlayerService.PlayerServiceBinder) iBinder).getBoundedService();
            if (musicPlayerService.isPlaying()) {
                peekSongName.setText(musicPlayerService.getSongName());
                bottomSheetSongName.setText(musicPlayerService.getSongName());
                bottomSheetSongArtist.setText(musicPlayerService.getArtistName());
                bottomSheetSeekbar.setMax(musicPlayerService.getDuration());
                updateSeekBarProgress();
                setPlayPauseDrawable(true);
            } else {
                String name = Preferences.SongDetails.getLastSongName(getApplicationContext());
                if (name != null) {
                    peekSongName.setText(name);
                    bottomSheetSongName.setText(name);
                    bottomSheetSongArtist.setText(musicPlayerService.getArtistName());
                    bottomSheetSeekbar.setMax(Preferences.SongDetails.getLastSongCurrentPosition(getApplicationContext()));
                }
                setPlayPauseDrawable(false);
            }
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

        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheet = BottomSheetBehavior.from(bottomSheetLayout);
        bottomPeek = findViewById(R.id.bottom_sheet_peek);

        peekFavorite = findViewById(R.id.peek_favorite);
        peekPlayPause = findViewById(R.id.peek_play_pause);
        peekSongName = findViewById(R.id.peek_song_name);
        peekSongName.setSelected(true);

        bottomSheetFavorite = findViewById(R.id.bottom_sheet_favorite);
        bottomSheetPlayPause = findViewById(R.id.bottom_sheet_play_pause);
        bottomSheetSongName = findViewById(R.id.bottom_sheet_song_name);
        bottomSheetSongName.setSelected(true);
        bottomSheetSongArtist = findViewById(R.id.bottom_sheet_song_artist);
        bottomSheetSongArtist.setSelected(true);
        bottomSheetSeekbar = findViewById(R.id.bottom_sheet_seek_bar);
//        bottomSheetSeekbar.setMax(musicPlayerService != null musicPlayerService.isPlaying() ? musicPlayerService.getDuration() : 100);

        bottomSheetSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicPlayerService != null && fromUser)
                    musicPlayerService.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fragmentManager = getSupportFragmentManager();
        askRequiredPermissions();

        peekPlayPause.setOnClickListener(v -> {
            if (musicPlayerService.isPlaying()) {
                musicPlayerService.pause();
                setPlayPauseDrawable(false);
            } else {
                musicPlayerService.startPlayer();
                setPlayPauseDrawable(true);
            }
        });

        bottomSheetLayout.setOnClickListener(v -> bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED));

        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomPeek.setAlpha(1f - (slideOffset * 1.5f));
            }
        });

        handler = new Handler();
    }

    public void updateSeekBarProgress() {

        runOnUiThread(() -> {
            if (musicPlayerService != null && musicPlayerService.isPlaying()) {
                int position = musicPlayerService.getCurrentPosition();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    bottomSheetSeekbar.setProgress(position, true);
                }
                bottomSheetSeekbar.setProgress(position);
            }
            handler.postDelayed(this::updateSeekBarProgress, 1000);
        });

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
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        })).start();
    }

    @Override
    public void onUpdateService(SongsModel previousModel, SongsModel currentModel, SongsModel nextModel, AndroidViewModel mViewModel) {

        musicPlayerService.setSongDetails(previousModel, currentModel, nextModel);
        startService();
        updateSeekBarProgress();
    }

    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.music:
                if (!(fragmentManager.findFragmentById(R.id.fragment_container) instanceof AllSongsFragment))
                    setFragment(new AllSongsFragment());
                break;
            case R.id.search:
                if (!(fragmentManager.findFragmentById(R.id.fragment_container) instanceof SearchFragment))
                    setFragment(new SearchFragment());
                break;
            case R.id.library:
                if (!(fragmentManager.findFragmentById(R.id.fragment_container) instanceof LibraryFragment))
                    setFragment(new LibraryFragment());
                break;
            case R.id.favorites:
                if (!(fragmentManager.findFragmentById(R.id.fragment_container) instanceof FavoritesFragment))
                    setFragment(new FavoritesFragment());
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
        localReceiver = new LocalReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_PLAY);
        filter.addAction(BROADCAST_ACTION_PAUSE);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
        Intent playerServiceIntent = new Intent(this, MusicPlayerService.class);
        bindService(playerServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if (!Preferences.DefaultSettings.isFirstLaunch(this)) {
            Preferences.DefaultSettings.setFirstLaunch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREFERENCES_ACTIVITY_STATE, true).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREFERENCES_ACTIVITY_STATE, false).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(mServiceConnection);
            isBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREFERENCES_ACTIVITY_STATE, false).apply();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
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

    private void setPlayPauseDrawable(boolean isPlaying) {
        peekPlayPause.setImageResource(isPlaying ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp);
        bottomSheetPlayPause.setImageResource(isPlaying ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp);
    }

    public class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent + "\n " + intent.getAction());
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case BROADCAST_ACTION_PLAY: {

                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFERENCES_ACTIVITY_STATE, false)) {
                            SongsModel model = (SongsModel) intent.getSerializableExtra("song_details");
                            peekSongName.setText(model.getTitle());
                            bottomSheetSongName.setText(model.getTitle());
                            bottomSheetSongArtist.setText(model.getArtist());
                            bottomSheetSeekbar.setMax(musicPlayerService.getDuration());
                            setPlayPauseDrawable(true);
                            musicPlayerService.createNotification(true);
                        }
                    }
                    break;
                    case BROADCAST_ACTION_PAUSE: {
                        setPlayPauseDrawable(true);
                        musicPlayerService.createNotification(false);
                    }
                    break;
                }
            }
        }
    }
}
