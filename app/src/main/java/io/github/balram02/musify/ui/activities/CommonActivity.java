package io.github.balram02.musify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.balram02.musify.R;
import io.github.balram02.musify.ui.adapters.SongsAdapter;
import io.github.balram02.musify.background.MusicPlayerService;
import io.github.balram02.musify.utils.Constants;
import io.github.balram02.musify.listeners.OnAdapterItemClickListener;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.utils.Preferences;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.utils.Constants.INTENT_ACTION_NEW_SONG;
import static io.github.balram02.musify.ui.activities.MainActivity.musicPlayerService;

public class CommonActivity extends AppCompatActivity implements OnAdapterItemClickListener {

    private TextView songsCount;
    private TextView albumArtistName;
    private ImageView albumArt;
    private RecyclerView recyclerView;

    private SongsAdapter songsAdapter;

    private SharedViewModel sharedViewModel;

    private String action;

    private List<SongsModel> currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityTheme();
        setContentView(R.layout.activity_common);

        songsCount = findViewById(R.id.songs_count);
        albumArtistName = findViewById(R.id.album_artist_name);
        albumArtistName.setSelected(true);
        albumArt = findViewById(R.id.album_art);
        recyclerView = findViewById(R.id.recycler_view);

        songsAdapter = new SongsAdapter(this);
        recyclerView.setAdapter(songsAdapter);
        songsAdapter.setOnItemClickListener(this);

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);

        Intent intent = getIntent();
        action = intent.getAction();
        String name = null;
        if (action != null) {
            if (action.equals("album")) {
                name = intent.getStringExtra("album_name");
                sharedViewModel.getSongsByAlbums(name).observe(this, songsModels -> {
                    songsAdapter.submitList(songsModels);
                    songsCount.setText(String.valueOf(songsModels.size()));
                    currentList = songsModels;
                });

            } else if (action.equals("artist")) {
                name = intent.getStringExtra("artist_name");
                sharedViewModel.getSongsByArtist(name).observe(this, songsModels -> {
                    songsAdapter.submitList(songsModels);
                    songsCount.setText(String.valueOf(songsModels.size()));
                    currentList = songsModels;
                });
            }
            albumArtistName.setText(name);

            Picasso.get()
                    .load(Constants.getAlbumArtUri(intent.getLongExtra("album_id", -1)))
                    .placeholder(R.drawable.ic_music_placeholder_white)
                    .into(albumArt, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            albumArt.setImageResource(R.drawable.ic_music_placeholder_white);
                        }
                    });
        }

        findViewById(R.id.back_arrow).setOnClickListener(view -> onBackPressed());

    }

    private void setActivityTheme() {
        if (Preferences.DefaultSettings.geActiveTheme(this) == Preferences.DEFAULT_DARK_THEME)
            getTheme().applyStyle(R.style.AppTheme, true);
        else
            getTheme().applyStyle(R.style.AppThemeLight, true);
    }

    @Override
    public void onItemClick(SongsModel model) {
        musicPlayerService.setPlayingFromFav(false);
        musicPlayerService.setSongDetails(currentList, model, sharedViewModel, false);
        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        serviceIntent.setAction(INTENT_ACTION_NEW_SONG);
        startService(serviceIntent);
    }
}
