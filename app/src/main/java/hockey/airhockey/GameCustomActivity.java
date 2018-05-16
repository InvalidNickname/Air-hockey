package hockey.airhockey;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
}