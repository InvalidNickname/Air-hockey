package hockey.airhockey;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static hockey.airhockey.MainActivity.HIDE_FLAGS;

public class GameCustomActivity extends AppCompatActivity {

    private GameCustomField gameCustomField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCustomField = new GameCustomField(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(gameCustomField);
        overridePendingTransition(0, 0);
    }

    private void hideSystemUI() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(HIDE_FLAGS);
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