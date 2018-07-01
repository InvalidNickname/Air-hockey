/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 01.07.18 15:49
 */

package hockey.airhockey;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static hockey.airhockey.MainActivity.HIDE_FLAGS;

public class GameCustomActivity extends AppCompatActivity {

    private GameCustomField gameCustomField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCustomField = new GameCustomField(this);
        if (!getIntent().getBooleanExtra("firstRun", true)) {
            gameCustomField.stopAnimation();
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(gameCustomField);
        overridePendingTransition(0, 0);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameCustomField.pauseDrawing();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameCustomField.resumeDrawing();
        hideSystemUI();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameCustomActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameCustomField.releaseMemory();
    }
}