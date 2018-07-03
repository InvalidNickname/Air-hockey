/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 14:00
 */

package hockey.airhockey;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import static hockey.airhockey.MainActivity.HIDE_FLAGS;

public class GameActivity extends BaseActivity {

    private GameField gameField;
    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameField = new GameField(GameActivity.this);
        Intent intent = getIntent();
        gameField.setMultiplayer(intent.getBooleanExtra("multiplayer", true));
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(gameField);
        overridePendingTransition(0, 0);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameField.pauseDrawing();
        if (!gameField.isGoingToActivity()) {
            startService(new Intent(this, MusicService.class).putExtra("pause", true));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        startService(new Intent(this, MusicService.class));
        gameField.resumeDrawing();
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis() & !gameField.isAbleToPause()) {
            gameField.setPause();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameField.releaseMemory();
    }
}