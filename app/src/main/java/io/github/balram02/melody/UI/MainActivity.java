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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

import io.github.balram02.melody.R;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    public final String TAG = MainActivity.this.getClass().getSimpleName();
    private final int PERMISSION_REQUEST_CODE = 101;
    private FragmentManager fragmentManager;
    public static BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        navigationView = findViewById(R.id.bottom_nav_view);
        navigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
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
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                setFragment(new AllSongsFragment());
            }
        } else {
            setFragment(new AllSongsFragment());
        }
    }

    private void setFragment(Fragment fragment) {
        new Thread(() -> runOnUiThread(() -> {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commitNow();
        })).start();
    }

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
}
