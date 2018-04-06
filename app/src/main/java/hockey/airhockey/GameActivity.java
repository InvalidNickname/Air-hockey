package hockey.airhockey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    GameField gameField;
    public static final String TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameField = new GameField(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(gameField);
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
    }
}