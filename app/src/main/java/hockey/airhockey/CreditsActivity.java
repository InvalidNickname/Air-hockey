package hockey.airhockey;

import android.content.Intent;
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

import static hockey.airhockey.MainActivity.HIDE_FLAGS;
import static hockey.airhockey.MainActivity.settings;
import static hockey.airhockey.MainActivity.volume;

public class CreditsActivity extends AppCompatActivity implements Runnable {

    private ScrollView scrollView;
    private Thread thread;
    private boolean isRunning;
    private long sec;
    private int current;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thread = new Thread(this);
        setContentView(R.layout.activity_credits);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(this, R.raw.night_runner);
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
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    public void run() {
        while (isRunning) {
            if (System.currentTimeMillis() - sec > 16000 / settings.height) {
                scrollView.smoothScrollBy(0, 1);
                sec = System.currentTimeMillis();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        thread = new Thread(this);
        sec = System.currentTimeMillis();
        thread.start();
        isRunning = true;
        hideSystemUI();
        mediaPlayer.seekTo(current);
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
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
            current = mediaPlayer.getCurrentPosition();
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
        ConstraintLayout.LayoutParams upperParams = new ConstraintLayout.LayoutParams((int) (0.48 * settings.width), settings.gateHeight);
        upperParams.leftToLeft = R.id.mainCredits;
        upperParams.rightToRight = R.id.mainCredits;
        upperParams.topToTop = R.id.mainCredits;
        upperParams.bottomToBottom = R.id.mainCredits;
        upperParams.verticalBias = 0;
        upperGate.setLayoutParams(upperParams);
        ConstraintLayout.LayoutParams lowerParams = new ConstraintLayout.LayoutParams((int) (0.48 * settings.width), settings.gateHeight);
        lowerParams.leftToLeft = R.id.mainCredits;
        lowerParams.rightToRight = R.id.mainCredits;
        lowerParams.topToTop = R.id.mainCredits;
        lowerParams.bottomToBottom = R.id.mainCredits;
        lowerParams.verticalBias = 1;
        lowerGate.setLayoutParams(lowerParams);
    }

    public void back(View view) {
        onBackPressed();
    }
}
