package com.example.zapiskiii;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "diary_prefs";
    private static final String KEY_THEME = "theme";
    private static final String KEY_FONT_SIZE = "font_size";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RadioGroup themeGroup = findViewById(R.id.radio_group_theme);
        RadioGroup fontGroup = findViewById(R.id.radio_group_font_size);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int theme = prefs.getInt(KEY_THEME, 0);
        int fontSize = prefs.getInt(KEY_FONT_SIZE, 1);

        // Theme
        ((RadioButton) themeGroup.getChildAt(theme)).setChecked(true);
        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int idx = group.indexOfChild(findViewById(checkedId));
            prefs.edit().putInt(KEY_THEME, idx).apply();
            applyTheme(idx);
        });
        applyTheme(theme);

        // Font size
        ((RadioButton) fontGroup.getChildAt(fontSize)).setChecked(true);
        fontGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int idx = group.indexOfChild(findViewById(checkedId));
            prefs.edit().putInt(KEY_FONT_SIZE, idx).apply();
        });
    }

    private void applyTheme(int idx) {
        switch (idx) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}
