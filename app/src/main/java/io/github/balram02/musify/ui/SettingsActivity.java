package io.github.balram02.musify.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.utils.Preferences;

import static io.github.balram02.musify.constants.Constants.TAG;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat themeSwitch;
    private CheckBox albumCheckbox;
    private static boolean setResultCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityTheme();
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumCheckbox = findViewById(R.id.album_art_checkbox);
        albumCheckbox.setChecked(!Preferences.DefaultSettings.getAlbumArtOnLockScreen(this));

        themeSwitch = findViewById(R.id.theme_switch_compat);
        themeSwitch.setChecked(Preferences.DefaultSettings.geActiveTheme(this) == Preferences.LIGHT_THEME);

    }

    private void setActivityTheme() {
        if (Preferences.DefaultSettings.geActiveTheme(this) == Preferences.DEFAULT_DARK_THEME)
            getTheme().applyStyle(R.style.AppTheme, true);
        else
            getTheme().applyStyle(R.style.AppTheme2, true);
    }

    public void themeSwitchCompat(View v) {
        Preferences.DefaultSettings.setActiveTheme(this,
                themeSwitch.isChecked() ? Preferences.LIGHT_THEME : Preferences.DEFAULT_DARK_THEME);
        setResultCode = true;
        recreate();
    }

    public void albumArtCheckbox(View v) {
        Preferences.DefaultSettings.setAlbumArtOnLockScreen(this, !albumCheckbox.isChecked());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: " + setResultCode);
        setResult(setResultCode ? Constants.INTENT_THEME_REQUEST : RESULT_CANCELED);
        super.onBackPressed();
    }
}
