package hockey.airhockey;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static hockey.airhockey.MainActivity.HIDE_FLAGS;

public class GameActivity extends AppCompatActivity {

    private GameField gameField;
    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameField = new GameField(GameActivity.this);
        Intent intent = getIntent();
        gameField.setMultiplayer(intent.getBooleanExtra("multiplayer",true));
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(gameField);
        overridePendingTransition(0, 0);
    }

    private void hideSystemUI() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameField.pauseDrawing();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameField.resumeDrawing();
        hideSystemUI();
        gameField.setTimeAfterPause();
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis() & !gameField.isAbleToPause()) {
            gameField.setPause();
        }
        backPressed = System.currentTimeMillis();
    }
}