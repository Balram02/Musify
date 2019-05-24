package io.github.balram02.melody.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import io.github.balram02.melody.R;

public class MainActivity extends AppCompatActivity {

/*
    private RecyclerView recyclerView;
    //    private CardView totalSongsCard;
    private SongsViewModel songsViewModel;

    private SwipeRefreshLayout refreshLayout;

    private SongsAdapter songsAdapter;

    public final String TAG = MainActivity.this.getClass().getSimpleName();
    private final int PERMISSION_REQUEST_CODE = 101;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout innerContainer;
    private LinearLayout outerContainer;
    private RelativeLayout bottomPeek;

    private ImageButton prev, play, next;
*/

    public final String TAG = MainActivity.this.getClass().getSimpleName();
    private final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        outerContainer = findViewById(R.id.outer_container);
        innerContainer = findViewById(R.id.inner_container);
        bottomPeek = findViewById(R.id.bottom_peek);

        prev = findViewById(R.id.prev_sheet);
        play = findViewById(R.id.play_sheet);
        next = findViewById(R.id.next_sheet);

        play.setOnClickListener(v -> {
            if (IS_PLAYING) {
                stopService(new Intent(this, PlayerService.class));
                IS_PLAYING = false;
                play.setImageDrawable(getResources().getDrawable(R.drawable.play_icon_black_24dp));
//                play.setImageDrawable(getResources().getDrawable(R.drawable.pause_icon_black_24dp));
            }
//            else
        });

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomPeek.setVisibility(View.GONE);
                } else {
                    if (bottomPeek.getVisibility() == View.GONE)
                        bottomPeek.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                Log.d("TAGGG", slideOffset + "");

                innerContainer.setTranslationY(-bottomSheet.getHeight() * slideOffset);
                if (slideOffset >= 0.0f && slideOffset <= 0.1) {
                    bottomPeek.setAlpha(0.9f);
                } else if (slideOffset > 0.1f && slideOffset <= 0.2f) {
                    bottomPeek.setAlpha(0.8f);
                } else if (slideOffset > 0.2f && slideOffset <= 0.3f) {
                    bottomPeek.setAlpha(0.7f);
                } else if (slideOffset > 0.3f && slideOffset <= 0.4f) {
                    bottomPeek.setAlpha(0.6f);
                } else if (slideOffset > 0.4f && slideOffset <= 0.5f) {
                    bottomPeek.setAlpha(0.5f);
                } else if (slideOffset > 0.5f && slideOffset <= 0.6f) {
                    bottomPeek.setAlpha(0.4f);
                } else if (slideOffset > 0.6f && slideOffset <= 0.7f) {
                    bottomPeek.setAlpha(0.3f);
                } else if (slideOffset > 0.7f && slideOffset <= 0.8f) {
                    bottomPeek.setAlpha(0.2f);
                } else if (slideOffset > 0.8f && slideOffset <= 0.9f) {
                    bottomPeek.setAlpha(0.1f);
//                    bottomPeek.setVisibility(View.VISIBLE);
//                } else if (slideOffset > 0.9f && slideOffset <= 1.0f) {
//                    bottomPeek.setAlpha(0.1f);
                } else {
                    bottomPeek.setAlpha(0);
//                    bottomPeek.setVisibility(View.GONE);
                }
            }
        });

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.spotifyGreen), getResources().getColor(R.color.spotifyBlack));

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
//        totalSongsCard = findViewById(R.id.card_view);

        songsAdapter = new SongsAdapter();

        songsAdapter.setOnItemClickListener(model -> {
            Intent intent = new Intent(this, PlayerService.class);
            intent.putExtra("song_name", model.getTitle());
            intent.putExtra("song_artist", model.getArtist());
            intent.putExtra("song_path", model.getPath());
            if (IS_PLAYING) {
                stopService(intent);
                IS_PLAYING = true;
                startService(intent);
                play.setImageDrawable(getResources().getDrawable(R.drawable.pause_icon_black_24dp));
            } else {
                IS_PLAYING = true;
                play.setImageDrawable(getResources().getDrawable(R.drawable.pause_icon_black_24dp));
                startService(intent);
            }
        });
*/
        askRequiredPermissions();

        /*        refreshLayout.setOnRefreshListener(() -> {
         *//*          getSharedPreferences(PREFERENCES_DETAILS, MODE_PRIVATE).edit().putBoolean(REFRESH_SONG_LIST, true).apply();
            songsViewModel.getAllSongs();
            new Handler().postDelayed(() -> {
                refreshLayout.setRefreshing(false);
            }, 4000);*//*
        });*/
    }

    private void askRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "asking permissions... ");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
//                setRecyclerViewObserver();
            }
        } else {
//            setRecyclerViewObserver();
        }
    }

/*    private void setRecyclerViewObserver() {
        songsViewModel = ViewModelProviders.of(this).get(SongsViewModel.class);
        songsViewModel.getAllSongs().observe(this, songsModels -> {
            ((TextView) findViewById(R.id.total_songs)).setText(songsModels.size() + " Songs found");
            songsAdapter.setSongs(songsModels);
            recyclerView.setAdapter(songsAdapter);
            startTotalSongsCardAnimation();
        });
    }*/

    private void startTotalSongsCardAnimation() {
/*
        ObjectAnimator animatorOut = ObjectAnimator.ofFloat(totalSongsCard, "translationY", -100f);
        animatorOut.setStartDelay(3000);
        animatorOut.setDuration(5000);
        animatorOut.start();
*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            setRecyclerViewObserver();
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

/*    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else
            super.onBackPressed();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

}
