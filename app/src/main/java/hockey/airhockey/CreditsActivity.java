/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 15:18
 */

package hockey.airhockey;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import java.io.IOException;

import static hockey.airhockey.MainActivity.HIDE_FLAGS;
import static hockey.airhockey.MainActivity.settings;
import static hockey.airhockey.MainActivity.volume;

public class CreditsActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private Thread thread;
    private Runnable runnable;
    private boolean isRunning;
    private long sec;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runnable = new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    if (System.currentTimeMillis() - sec > 16000 / settings.height) {
                        scrollView.smoothScrollBy(0, 1);
                        sec = System.currentTimeMillis();
                    }
                }
            }
        };
        // поток для прокрутки титров
        thread = new Thread(runnable);
        setContentView(R.layout.activity_credits);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = getAssets().openFd("music/night_runner.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setVolume(settings.baseVolume * volume, settings.baseVolume * volume);
        mediaPlayer.setLooping(true);
        overridePendingTransition(0, 0);
        scrollView = findViewById(R.id.scrollView);
        Space space = findViewById(R.id.space);
        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(0, settings.height);
        space.setLayoutParams(spaceParams);
        sec = System.currentTimeMillis();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        drawGates();
        thread.start();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        thread = new Thread(runnable);
        sec = System.currentTimeMillis();
        thread.start();
        isRunning = true;
        hideSystemUI();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreditsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void drawGates() {
        ImageView upperGate = findViewById(R.id.upperGate);
        ImageView lowerGate = findViewById(R.id.lowerGate);
        ConstraintLayout.LayoutParams upperParams = (ConstraintLayout.LayoutParams) upperGate.getLayoutParams();
        upperParams.height = settings.gateHeight;
        upperParams.width = (int) (settings.width * 0.74) - (int) (settings.width * 0.26);
        upperGate.setLayoutParams(upperParams);
        ConstraintLayout.LayoutParams lowerParams = (ConstraintLayout.LayoutParams) lowerGate.getLayoutParams();
        lowerParams.height = settings.gateHeight;
        lowerParams.width = (int) (settings.width * 0.74) - (int) (settings.width * 0.26);
        lowerGate.setLayoutParams(lowerParams);
    }

    public void back(View view) {
        onBackPressed();
    }
}
