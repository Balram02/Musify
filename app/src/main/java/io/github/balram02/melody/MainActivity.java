package io.github.balram02.melody;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private TextView totalSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setHasFixedSize(true);
        List<SongListModel> listModels = getAllSongsList();
        totalSongs = findViewById(R.id.total_songs);
        totalSongs.setText("Total songs - " + listModels.size());
        SongListAdapater songListAdapater = new SongListAdapater(listModels);
        recyclerView.setAdapter(songListAdapater);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private List<SongListModel> getAllSongsList() {
        try {
            return new SongsAsyncTask(this).execute().get();
        } catch (Exception e) {
            Log.d("TAGGG", e.toString());
            return null;
        }
    }

    static class SongsAsyncTask extends AsyncTask<Void, Void, List<SongListModel>> {

        private WeakReference<MainActivity> weakReference;

        SongsAsyncTask(MainActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        protected List<SongListModel> doInBackground(Void... voids) {

            List<SongListModel> songs = new ArrayList<>();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

//            String[] strings = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.ARTIST};
            Cursor cursor = weakReference.get().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {

                int title = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                do {
                    String songTitle = cursor.getString(title);
                    String songArtist = cursor.getString(artist);
                    String songData = cursor.getString(data);
                    SongListModel listModel = new SongListModel(songTitle, songArtist, songData);
                    Log.d("TAGGG", "" + title + artist + data);
                    songs.add(listModel);
                } while (cursor.moveToNext());
            }
            return songs;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}