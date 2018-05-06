package hockey.airhockey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class GameCustomActivity extends AppCompatActivity {

    private GameCustomField gameCustomField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCustomField = new GameCustomField(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(gameCustomField);
        overridePendingTransition(0, 0);
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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameCustomActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}