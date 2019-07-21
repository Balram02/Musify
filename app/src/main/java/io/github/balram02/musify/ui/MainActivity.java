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
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.Random;

import io.github.balram02.musify.R;
import io.github.balram02.musify.background.MusicPlayerService;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.listeners.FragmentListener;
import io.github.balram02.musify.listeners.MusicPlayerServiceListener;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.utils.Preferences;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_NEW_SONG;
import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_PAUSE;
import static io.github.balram02.musify.constants.Constants.INTENT_ACTION_PLAY;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_ACTIVITY_STATE;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_REPEAT_STATE_ALL;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_REPEAT_STATE_NONE;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_REPEAT_STATE_ONE;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_SHUFFLE_STATE_NO;
import static io.github.balram02.musify.constants.Constants.PREFERENCES_SHUFFLE_STATE_YES;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, MusicPlayerServiceListener, FragmentListener {

    private final String TAG = Constants.TAG;

    private SharedViewModel sharedViewModel;

    private TextView peekSongName;
    private ImageView peekFavorite;
    private ImageView peekPlayPause;
    private ImageView bottomSheetRepeat;
    private ImageView bottomSheetShuffle;
    private ImageView bottomPeekUpArrow;

    private TextView bottomSheetSongName;
    private TextView bottomSheetSongArtist;
    private ImageView bottomSheetPlayPause;
    private ImageView bottomSheetFavorite;
    private SeekBar bottomSheetSeekbar;
    private TextView bottomSheetSongCurrentPosition;
    private TextView bottomSheetSongDuration;
    private ImageView bottomSheetAlbumArt;

    private FragmentManager fragmentManager;
    private AllSongsFragment allSongsFragment = new AllSongsFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private LibraryFragment libraryFragment = new LibraryFragment();
    private FavoritesFragment favoritesFragment = new FavoritesFragment();
    public CommonFragment commonFragment = new CommonFragment();
    private Fragment activeFragment;
    private BottomNavigationView navigationView;

    private BottomSheetBehavior bottomSheet;
    private RelativeLayout bottomSheetLayout;
    private ConstraintLayout bottomSheetConstraintLayout;
    private RelativeLayout bottomPeek;

    private SongsModel currentSongModel;

    private boolean isBound;
    private Handler handler;

    public static MusicPlayerService musicPlayerService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            isBound = true;
            musicPlayerService = ((MusicPlayerService.PlayerServiceBinder) iBinder).getBoundService();
            musicPlayerService.getMediaControllerCompat().registerCallback(controllerCompatCallback);
            setUpLastDetails();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            isBound = false;
        }
    };

    private MediaControllerCompat.Callback controllerCompatCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            if (bottomSheetLayout.getVisibility() == View.GONE) {
                bottomSheetLayout.setVisibility(View.VISIBLE);
            }

            SongsModel model = new Gson().fromJson(metadata.getString("song_model_object"), SongsModel.class);
            setFavoritesDrawable(model.isFavorite());
            setAlbumArt(model.getAlbumId());
            peekSongName.setText(model.getTitle());
            bottomSheetSongName.setText(model.getTitle());
            bottomSheetSongArtist.setText(model.getArtist());
            bottomSheetSeekbar.setMax(musicPlayerService.getDuration());
            bottomSheetSongDuration.setText(Constants.convertMilliseconds(model.getDuration()));
            addObserverOnFavorite();
            updateSeekBarProgress();
            model.setLastAccessedTimestamp(new Date().getTime());
            sharedViewModel.update(model);

            currentSongModel = model;
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            setPlayPauseDrawable(state.getState() == PlaybackStateCompat.STATE_PLAYING);
        }
    };

    private void setActivityTheme() {
        if (Preferences.DefaultSettings.geActiveTheme(this) == Preferences.DEFAULT_DARK_THEME)
            getTheme().applyStyle(R.style.AppTheme, true);
        else
            getTheme().applyStyle(R.style.AppThemeLight, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityTheme();

        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        navigationView = findViewById(R.id.bottom_nav_view);
        navigationView.setOnNavigationItemSelectedListener(this);

        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetConstraintLayout = findViewById(R.id.constraintLayout);
        bottomSheet = BottomSheetBehavior.from(bottomSheetLayout);
        bottomPeek = findViewById(R.id.bottom_sheet_peek);

        peekFavorite = findViewById(R.id.peek_favorite);
        peekPlayPause = findViewById(R.id.peek_play_pause);
        peekSongName = findViewById(R.id.peek_song_name);
        peekSongName.setSelected(true);
        bottomPeekUpArrow = findViewById(R.id.bottom_peek_up_arrow);

        bottomSheetAlbumArt = findViewById(R.id.bottom_sheet_album_art);
        bottomSheetFavorite = findViewById(R.id.bottom_sheet_favorite);
        bottomSheetPlayPause = findViewById(R.id.bottom_sheet_play_pause);
        bottomSheetSongName = findViewById(R.id.bottom_sheet_song_name);
        bottomSheetSongName.setSelected(true);
        bottomSheetSongArtist = findViewById(R.id.bottom_sheet_song_artist);
        bottomSheetSongArtist.setSelected(true);
        bottomSheetSeekbar = findViewById(R.id.bottom_sheet_seek_bar);
        bottomSheetRepeat = findViewById(R.id.bottom_sheet_repeat);
        bottomSheetShuffle = findViewById(R.id.bottom_sheet_shuffle);
        bottomSheetSongCurrentPosition = findViewById(R.id.bottom_sheet_song_current_position);
        bottomSheetSongCurrentPosition.setSelected(false);
        bottomSheetSongDuration = findViewById(R.id.bottom_sheet_song_duration);
        bottomSheetSongDuration.setSelected(false);

        fragmentManager = getSupportFragmentManager();
        handler = new Handler();

        askRequiredPermissions();

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

        bottomSheetLayout.setOnClickListener(v -> {
            if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            else if (bottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomPeekUpArrow.setRotation(slideOffset * 180f);
                bottomPeek.setAlpha(1f - (slideOffset * 1.5f));
                bottomSheetConstraintLayout.setAlpha(slideOffset * 1.2f);
            }
        });

    }

    public void setUpLastDetails() {

        boolean shuffleState = Preferences.DefaultSettings.getShuffleState(this);

        if (musicPlayerService != null && musicPlayerService.isPlaying()) {

            if (bottomSheetLayout.getVisibility() == View.GONE)
                bottomSheetLayout.setVisibility(View.VISIBLE);

            setFavoritesDrawable(musicPlayerService.isFavorite());
            peekSongName.setText(musicPlayerService.getSongName());
            bottomSheetSongName.setText(musicPlayerService.getSongName());
            bottomSheetSongArtist.setText(musicPlayerService.getArtistName());
            bottomSheetSeekbar.setMax(musicPlayerService.getDuration());
            bottomSheetSongDuration.setText(Constants.convertMilliseconds(musicPlayerService.getDuration()));
            setAlbumArt(musicPlayerService.getAlbumId());
            updateSeekBarProgress();
            setPlayPauseDrawable(true);
            currentSongModel = musicPlayerService.getSongModel();

        } else {
            SongsModel lastSongModel = Preferences.SongDetails.getLastSongDetails(this);
            if (lastSongModel != null) {
                setFavoritesDrawable(lastSongModel.isFavorite());
                peekSongName.setText(lastSongModel.getTitle());
                bottomSheetSongName.setText(lastSongModel.getTitle());
                bottomSheetSongArtist.setText(lastSongModel.getArtist());
                bottomSheetSeekbar.setMax((int) lastSongModel.getDuration());
                bottomSheetSeekbar.setProgress(Preferences.SongDetails.getLastSongCurrentPosition(this));
                bottomSheetLayout.setVisibility(View.VISIBLE);
                bottomSheetSongDuration.setText(Constants.convertMilliseconds(lastSongModel.getDuration()));
                bottomSheetSongCurrentPosition.setText(
                        Constants.convertMilliseconds(Preferences.SongDetails.getLastSongCurrentPosition(this)));
                setAlbumArt(lastSongModel.getAlbumId());
                setQueueListInService();
                currentSongModel = lastSongModel;
            } else {
                bottomSheetLayout.setVisibility(View.GONE);
            }

            setPlayPauseDrawable(false);
        }

        addObserverOnFavorite();

        setShuffleDrawable(shuffleState);
        setRepeatDrawable(Preferences.DefaultSettings.getRepeatState(this));
    }

    public void addObserverOnFavorite() {

        String path = "";
        if (musicPlayerService != null && musicPlayerService.getSongModel() != null) {
            path = musicPlayerService.getSongPath();
        } else if (Preferences.SongDetails.getLastSongDetails(this) != null) {
            path = Preferences.SongDetails.getLastSongDetails(this).getPath();
        }

        if (!path.equals("") || !path.isEmpty()) {
            sharedViewModel.isFavorite(path).observe(this, isFavorite -> {
                SongsModel model = Preferences.SongDetails.getLastSongDetails(this);
                if (musicPlayerService != null && musicPlayerService.getSongModel() != null) {
                    musicPlayerService.setFavorite(isFavorite);
                } else if (model != null) {
                    model.setFavorite(isFavorite);
                    Preferences.SongDetails.setLastSongDetails(this, model);
                }
                setFavoritesDrawable(isFavorite);
            });
        }
    }

    public void updateSeekBarProgress() {

        boolean isForeground = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREFERENCES_ACTIVITY_STATE, false);

        runOnUiThread(() -> {
            if (musicPlayerService != null && musicPlayerService.isPlaying() && isForeground) {
                int position = musicPlayerService.getCurrentPosition();
                bottomSheetSongCurrentPosition.setText(Constants.convertMilliseconds(position));
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
                sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
                addHiddenFragments();
                setFragment(allSongsFragment);
            }
        } else {
            sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
            addHiddenFragments();
            setFragment(allSongsFragment);
        }
    }

    private void startMyService(String action) {
        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        serviceIntent.setAction(action);
        startService(serviceIntent);
    }

    private void addHiddenFragments() {
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragment_container, allSongsFragment, "all_songs_fragment").hide(allSongsFragment).commit();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragment_container, libraryFragment, "library_fragment").hide(libraryFragment).commit();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragment_container, searchFragment, "search_fragment").hide(searchFragment).commit();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragment_container, favoritesFragment, "favorites_fragment").hide(favoritesFragment).commit();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(R.id.fragment_container, commonFragment, "common_fragment").hide(commonFragment).commit();
    }

    @Override
    public void setCommonFragmentType(String fragmentType) {
        setFragment(commonFragment);
        setTitle(fragmentType.equals(FragmentListener.ALBUM_FRAGMENT) ? "Albums" : "Artists");
        commonFragment.setRecyclerViewAdapterType(fragmentType);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setFragment(Fragment fragment) {
        new Thread(() -> runOnUiThread(() -> {

            if (activeFragment == null) {
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .show(fragment).commitAllowingStateLoss();
            } else {
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .hide(activeFragment).show(fragment).commitAllowingStateLoss();
            }
            activeFragment = fragment;


            if (fragment instanceof AllSongsFragment)
                setTitle(R.string.app_name);
            else if (fragment instanceof LibraryFragment)
                setTitle("Library");
            else if (fragment instanceof FavoritesFragment)
                setTitle("Favorites");
            else if (fragment instanceof SearchFragment)
                setTitle("Search");

            if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        })).start();
    }

    @Override
    public void onUpdateService(SongsModel currentModel) {
        boolean state = Preferences.DefaultSettings.getShuffleState(this);
        if (state) {
            musicPlayerService.setSongDetails(sharedViewModel.getShuffleSongsQueue(), currentModel, sharedViewModel, true);
        } else {
            musicPlayerService.setSongDetails(sharedViewModel.getAllSongsQueue(), currentModel, sharedViewModel, false);
        }
        Log.d(TAG, "onUpdateService: shuffle state = " + state);
        musicPlayerService.setPlayingFromFav(false);
        startMyService(INTENT_ACTION_NEW_SONG);
    }

    @Override
    public void onPlayFromFavorites(SongsModel currentModel, boolean shuffleFav) {
        List<SongsModel> list;
        if (shuffleFav) {
            list = sharedViewModel.getFavoritesShuffleQueueList();
            if (currentModel == null && list.size() != 0)
                currentModel = list.get(new Random().nextInt(list.size()));
            musicPlayerService.setSongDetails(list, currentModel, sharedViewModel, true);
        } else {
            musicPlayerService.setSongDetails(sharedViewModel.getFavoritesQueueList(), currentModel, sharedViewModel, false);
        }
        musicPlayerService.setPlayingFromFav(true);
        startMyService(INTENT_ACTION_NEW_SONG);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.music:
                if (!allSongsFragment.isVisible())
                    setFragment(allSongsFragment);
                break;
            case R.id.search:
                if (!searchFragment.isVisible())
                    setFragment(searchFragment);
                break;
            case R.id.library:
                if (!libraryFragment.isVisible())
                    setFragment(libraryFragment);
                break;
            case R.id.favorites:
                if (!favoritesFragment.isVisible())
                    setFragment(favoritesFragment);
                break;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: MainActivity");

        IntentFilter globalFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mNoisyReceiver, globalFilter);

        Intent playerServiceIntent = new Intent(this, MusicPlayerService.class);
        bindService(playerServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume: ");
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREFERENCES_ACTIVITY_STATE, true).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: MainActivity");
        musicPlayerService.getMediaControllerCompat().unregisterCallback(controllerCompatCallback);
        if (isBound) {
            unbindService(mServiceConnection);
            isBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREFERENCES_ACTIVITY_STATE, false).apply();
        unregisterReceiver(mNoisyReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
            addHiddenFragments();
            setFragment(allSongsFragment);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission denied")
                    .setMessage("Storage permissions are needed for this app to work properly.\nApp will close if canceled")
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        finish();
                    }).setPositiveButton("Ok", (dialog, which) -> {
                askRequiredPermissions();
            });
            builder.setCancelable(false).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        else if (activeFragment instanceof CommonFragment) {
            setFragment(libraryFragment);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else if (!(activeFragment instanceof AllSongsFragment))
            navigationView.setSelectedItemId(R.id.music);
        else
            super.onBackPressed();
    }

    public void setAlbumArt(long albumArtId) {

        Bitmap bitmap = Constants.getAlbumArt(this, albumArtId);
        if (bitmap != null) {
            bottomSheetAlbumArt.setImageBitmap(bitmap);
            bottomSheetAlbumArt.setBackground(null);
            bottomSheetAlbumArt.setPadding(0, 0, 0, 0);
        } else {
            bottomSheetAlbumArt.setImageResource(R.drawable.ic_music_placeholder_white);
            bottomSheetAlbumArt.setBackground(getDrawable(R.drawable.background_square_stroke_white_16dp));
            bottomSheetAlbumArt.setPadding(30, 30, 30, 30);
        }
    }

    public void setQueueListInService() {
        boolean isShuffled = Preferences.DefaultSettings.getShuffleState(this);
        musicPlayerService.setSongsQueueList(isShuffled, isShuffled ?
                musicPlayerService.isPlayingFromFav() ? sharedViewModel.getFavoritesShuffleQueueList() : sharedViewModel.getShuffleSongsQueue() :
                musicPlayerService.isPlayingFromFav() ? sharedViewModel.getFavoritesQueueList() : sharedViewModel.getAllSongsQueue());
    }

    public void showCurrentSongMenu(View view) {

        BottomSheetDialog dialogFragment = new BottomSheetDialog(this);
        dialogFragment.setContentView(R.layout.song_menu_layout);

        ((TextView) dialogFragment.findViewById(R.id.title)).setText(currentSongModel.getTitle());
        ImageView favImage = dialogFragment.findViewById(R.id.fav_img);
        TextView favText = dialogFragment.findViewById(R.id.fav_text);

        boolean isFav = currentSongModel.isFavorite();
        if (isFav) {
            favText.setText("Remove from favorites");
            favImage.setImageResource((R.drawable.ic_favorite_border_white_24dp));
        } else {
            favText.setText("Add to favorites");
            favImage.setImageResource(R.drawable.ic_favorite_filled_white_24dp);
        }

        dialogFragment.findViewById(R.id.add_to_fav).setOnClickListener(v1 -> {
            currentSongModel.setFavorite(!isFav);
            sharedViewModel.update(currentSongModel);
            dialogFragment.dismiss();
        });

        dialogFragment.findViewById(R.id.song_info_layout).setOnClickListener(v1 -> {

            BottomSheetDialog infoDialogFragment = new BottomSheetDialog(this);
            infoDialogFragment.setContentView(R.layout.song_info_layout);

            Uri uri = Constants.getAlbumArtUri(currentSongModel.getAlbumId());

            Picasso.get().load(uri).into(infoDialogFragment.findViewById(R.id.info_album_art), new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    ((ImageView) infoDialogFragment.findViewById(R.id.info_album_art))
                            .setImageResource(R.drawable.ic_music_placeholder_white);
                }
            });

            ((TextView) infoDialogFragment.findViewById(R.id.info_song_album)).setText(currentSongModel.getAlbum());
            ((TextView) infoDialogFragment.findViewById(R.id.info_song_title)).setText(currentSongModel.getTitle());
            ((TextView) infoDialogFragment.findViewById(R.id.info_song_artist)).setText(currentSongModel.getArtist());
            ((TextView) infoDialogFragment.findViewById(R.id.info_song_path)).setText(currentSongModel.getPath());
            infoDialogFragment.findViewById(R.id.info_back_arrow).setOnClickListener(v2 -> infoDialogFragment.dismiss());

            infoDialogFragment.show();

        });

        dialogFragment.show();


    }

    public void onClickPreviousButton(View view) {
        if (musicPlayerService != null) {
            musicPlayerService.playPrevious();
        }
    }

    public void onClickPlayPauseButton(View view) {
        if (musicPlayerService.isPlaying()) {
            startMyService(INTENT_ACTION_PAUSE);
        } else {
            startMyService(INTENT_ACTION_PLAY);
        }
    }

    public void onClickNextButton(View view) {
        if (musicPlayerService != null) {
            musicPlayerService.playNext();
        }
    }

    public void onClickFavoriteButton(View view) {

        boolean isFavorite;
        SongsModel model;

        if (musicPlayerService != null && musicPlayerService.getSongModel() != null) {
            isFavorite = musicPlayerService.isFavorite();
            model = musicPlayerService.getSongModel();
            model.setFavorite(!isFavorite);
            sharedViewModel.update(model);
            musicPlayerService.setFavorite(!isFavorite);
        } else {
            isFavorite = Preferences.SongDetails.getLastSongDetails(this).isFavorite();
            model = Preferences.SongDetails.getLastSongDetails(this);
            model.setFavorite(!isFavorite);
            sharedViewModel.update(model);
            Preferences.SongDetails.setLastSongDetails(this, model);
        }

        setFavoritesDrawable(!isFavorite);
    }

    public void onClickRepeatButton(View view) {
        int state = PREFERENCES_REPEAT_STATE_NONE;
        switch (Preferences.DefaultSettings.getRepeatState(this)) {
            case PREFERENCES_REPEAT_STATE_NONE:
                state = PREFERENCES_REPEAT_STATE_ALL;
                break;
            case PREFERENCES_REPEAT_STATE_ALL:
                state = PREFERENCES_REPEAT_STATE_ONE;
                break;
            case PREFERENCES_REPEAT_STATE_ONE:
                state = PREFERENCES_REPEAT_STATE_NONE;
                break;
        }
        Preferences.DefaultSettings.setRepeatState(this, state);
        setRepeatDrawable(state);
        if (musicPlayerService != null && musicPlayerService.isPlaying()) {
            musicPlayerService.setLooping(state == PREFERENCES_REPEAT_STATE_ONE);
        }
    }

    public void onClickShuffleButton(View view) {
        boolean state = Preferences.DefaultSettings.getShuffleState(this);
        if (!state) {
            Preferences.DefaultSettings.setShuffleState(this, PREFERENCES_SHUFFLE_STATE_YES);
            setShuffleDrawable(PREFERENCES_SHUFFLE_STATE_YES);
        } else {
            Preferences.DefaultSettings.setShuffleState(this, PREFERENCES_SHUFFLE_STATE_NO);
            setShuffleDrawable(PREFERENCES_SHUFFLE_STATE_NO);
        }
        setQueueListInService();
    }

    private void setPlayPauseDrawable(boolean isPlaying) {
        peekPlayPause.setImageResource(isPlaying ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp);
        bottomSheetPlayPause.setImageResource(isPlaying ? R.drawable.pause_icon_white_24dp : R.drawable.play_icon_white_24dp);
    }

    private void setFavoritesDrawable(boolean isFavorite) {
        peekFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite_filled_white_24dp : R.drawable.ic_favorite_border_white_24dp);
        bottomSheetFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite_filled_white_24dp : R.drawable.ic_favorite_border_white_24dp);
    }

    private void setRepeatDrawable(int state) {
        switch (state) {
            case PREFERENCES_REPEAT_STATE_NONE:
                bottomSheetRepeat.setImageResource(R.drawable.ic_repeat_none_24dp);
                break;
            case PREFERENCES_REPEAT_STATE_ALL:
                bottomSheetRepeat.setImageResource(R.drawable.ic_repeat_all_24dp);
                break;
            case PREFERENCES_REPEAT_STATE_ONE:
                bottomSheetRepeat.setImageResource(R.drawable.ic_repeat_one_24dp);
                break;
        }
    }

    private void setShuffleDrawable(boolean state) {
        if (state)
            bottomSheetShuffle.setImageResource(R.drawable.ic_shuffle_yes_24dp);
        else
            bottomSheetShuffle.setImageResource(R.drawable.ic_shuffle_no_24dp);
    }

    private BroadcastReceiver mNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (musicPlayerService != null && musicPlayerService.isPlaying()) {
                musicPlayerService.pause();
            }
        }
    };
}
