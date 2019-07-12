package io.github.balram02.musify.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import io.github.balram02.musify.R;
import io.github.balram02.musify.utils.Preferences;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat themeSwitch;
    private CheckBox albumCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityTheme();
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");

        albumCheckbox = findViewById(R.id.album_art_checkbox);
        albumCheckbox.setChecked(!Preferences.DefaultSettings.getAlbumArtOnLockScreen(this));

        themeSwitch = findViewById(R.id.theme_switch_compat);
        themeSwitch.setChecked(Preferences.DefaultSettings.geActiveTheme(this) == Preferences.LIGHT_THEME);

        TextView contributor = findViewById(R.id.become_contributor);
        Linkify.addLinks(contributor, Linkify.WEB_URLS);
        contributor.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.rate_app).setOnClickListener(view -> {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

    }

    private void setActivityTheme() {
        if (Preferences.DefaultSettings.geActiveTheme(this) == Preferences.DEFAULT_DARK_THEME)
            getTheme().applyStyle(R.style.AppTheme, true);
        else
            getTheme().applyStyle(R.style.AppThemeLight, true);
    }

    public void themeSwitchCompat(View v) {
        Preferences.DefaultSettings.setActiveTheme(this,
                themeSwitch.isChecked() ? Preferences.LIGHT_THEME : Preferences.DEFAULT_DARK_THEME);
        Toast.makeText(this, "Changes will reflect after restart", Toast.LENGTH_LONG).show();
    }

    public void albumArtCheckbox(View v) {
        Preferences.DefaultSettings.setAlbumArtOnLockScreen(this, !albumCheckbox.isChecked());
        Toast.makeText(this, "Kindly pause and play once to see updated settings", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
