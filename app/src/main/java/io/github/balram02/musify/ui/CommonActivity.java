package io.github.balram02.musify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import io.github.balram02.musify.R;
import io.github.balram02.musify.adapters.SongsAdapter;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.listeners.OnAdapterItemClickListener;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.utils.Preferences;
import io.github.balram02.musify.viewModels.SharedViewModel;

public class CommonActivity extends AppCompatActivity implements OnAdapterItemClickListener {

    private TextView songsCount;
    private TextView albumArtistName;
    private ImageView albumArt;
    private RecyclerView recyclerView;

    private SongsAdapter songsAdapter;

    private SharedViewModel sharedViewModel;

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

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);

        Intent intent = getIntent();
        String action = intent.getAction();
        String name = null;
        if (action != null) {
            if (action.equals("album")) {
                name = intent.getStringExtra("album_name");
                sharedViewModel.getSongsByAlbums(name).observe(this, songsModels -> {
                    songsAdapter.submitList(songsModels);
                    songsCount.setText(String.valueOf(songsModels.size()));
                });

            } else if (action.equals("artist")) {
                name = intent.getStringExtra("artist_name");
                sharedViewModel.getSongsByArtist(name).observe(this, songsModels -> {
                    songsAdapter.submitList(songsModels);
                    songsCount.setText(String.valueOf(songsModels.size()));
                });
            }
            albumArtistName.setText(name);

            Picasso.get()
                    .load(Constants.getAlbumArtUri(intent.getLongExtra("album_id", -1)))
                    .placeholder(R.drawable.ic_music_placeholder_white)
                    .into(albumArt);
        }

        findViewById(R.id.back_arrow).setOnClickListener(view -> onBackPressed());

    }

    private void setActivityTheme() {
        if (Preferences.DefaultSettings.geActiveTheme(this) == Preferences.DEFAULT_DARK_THEME)
            getTheme().applyStyle(R.style.AppTheme, true);
        else
            getTheme().applyStyle(R.style.AppTheme2, true);
    }

    @Override
    public void onItemClick(SongsModel model) {
        Toast.makeText(this, "Kya hai be", Toast.LENGTH_SHORT).show();
    }
}
