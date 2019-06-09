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
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.ViewModels.AllSongsViewModel;
import io.github.balram02.musify.ViewModels.FavoritesViewModel;
import io.github.balram02.musify.background.MusicPlayerService;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.listeners.MusicPlayerServiceListener;
import io.github.balram02.musify.utils.Preferences;

import static io.github.balram02.musify.constants.Constants.ACTION_NEW_SONG;
import static io.github.balram02.musify.constants.Constants.ACTION_PAUSE;
import static io.github.balram02.musify.constants.Constants.ACTION_PLAY;
import static io.github.balram02.musify.constants.Constants.BROADCAST_ACTION_PAUSE;
import static io.github.balram02.musify.constants.Constants.BROADCAST_ACTION_PLAY;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_ACTIVITY_STATE;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, MusicPlayerServiceListener {

    public final String TAG = Constants.TAG;

    private LocalReceiver localReceiver;

    public TextView peekSongName;
    private ImageView peekFavorite;
    public ImageView peekPlayPause;

    public TextView bottomSheetSongName;
    public TextView bottomSheetSongArtist;
    public ImageView bottomSheetPlayPause;
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
            musicPlayerService = ((MusicPlayerService.PlayerServiceBinder) iBinder).getBoundService();
//            setUpLastDetails();
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

        bottomSheetSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicPlayerService != null && fromUser)
                    musicPlayerService.seekTo(progress);
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

    public void setUpLastDetails() {

        if (isBound && musicPlayerService != null && musicPlayerService.isPlaying()) {

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
                bottomSheetSongArtist.setText(Preferences.SongDetails.getLastSongArtist(getApplicationContext()));
                bottomSheetSeekbar.setMax((int) Preferences.SongDetails.getLastSongMaxDuration(getApplicationContext()));
                bottomSheetSeekbar.setProgress(Preferences.SongDetails.getLastSongCurrentPosition(getApplicationContext()));
            } else {
                SongsModel firstSongModel = ViewModelProviders.of(this).get(AllSongsViewModel.class).getFirstSong().getValue();
                Log.d(TAG, "setUpLastDetails: " + firstSongModel);
                if (firstSongModel != null) {
                    peekSongName.setText(firstSongModel.getTitle());
                    bottomSheetSongName.setText(firstSongModel.getTitle());
                    bottomSheetSongArtist.setText(firstSongModel.getArtist());
                    bottomSheetSeekbar.setMax((int) firstSongModel.getDuration());
                }
            }
            setPlayPauseDrawable(false);
        }
    }


    public void updateSeekBarProgress() {
        runOnUiThread(() -> {
            if (musicPlayerService != null && musicPlayerService.isPlaying()) {
                int position = musicPlayerService.getCurrentPosition();
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

    private void startService(String action) {
        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        serviceIntent.setAction(action);
        startService(serviceIntent);
    }

    public void setFragment(Fragment fragment) {
        new Thread(() -> runOnUiThread(() -> {
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        })).start();
    }

    @Override
    public void onUpdateService(SongsModel currentModel, AndroidViewModel mViewModel) {
        if (mViewModel instanceof AllSongsViewModel || mViewModel instanceof FavoritesViewModel)
            musicPlayerService.setSongDetails(currentModel, mViewModel);
        startService(ACTION_NEW_SONG);
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
        Log.d(TAG, "onStart: ");
        localReceiver = new LocalReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_PLAY);
        filter.addAction(BROADCAST_ACTION_PAUSE);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
        Intent playerServiceIntent = new Intent(this, MusicPlayerService.class);
        bindService(playerServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREFERENCES_ACTIVITY_STATE, true).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREFERENCES_ACTIVITY_STATE, false).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: MainActivity");
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
//            Preferences.DefaultSettings.setFirstLaunch(this, true);
            setFragment(new AllSongsFragment());
//            Preferences.DefaultSettings.setFirstLaunch(this, false);
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

    public void onClickPlayPauseButton(View view) {
        if (musicPlayerService.isPlaying()) {
            startService(ACTION_PAUSE);
        } else {
            startService(ACTION_PLAY);
        }
    }

    private void setPlayPauseDrawable(boolean isPlaying) {
        peekPlayPause.setImageResource(isPlaying ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp);
        bottomSheetPlayPause.setImageResource(isPlaying ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp);
    }

    public class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case BROADCAST_ACTION_PLAY:

                        SongsModel model = (SongsModel) intent.getSerializableExtra("song_details");
                        peekSongName.setText(model.getTitle());
                        bottomSheetSongName.setText(model.getTitle());
                        bottomSheetSongArtist.setText(model.getArtist());
                        bottomSheetSeekbar.setMax(musicPlayerService.getDuration());
                        updateSeekBarProgress();
                        setPlayPauseDrawable(true);
                        break;

                    case BROADCAST_ACTION_PAUSE:
                        setPlayPauseDrawable(false);
                        break;

                }
            }
        }
    }
}
