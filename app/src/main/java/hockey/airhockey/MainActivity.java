package hockey.airhockey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    GameField gameField;
    public static final String TAG = "MAIN";
    static int width, height, playerScale, puckScale, maxSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        playerScale = getResources().getDisplayMetrics().widthPixels / getResources().getInteger(R.integer.player_scale);
        puckScale = getResources().getDisplayMetrics().widthPixels / getResources().getInteger(R.integer.puck_scale);
        maxSpeed = getResources().getDisplayMetrics().widthPixels / getResources().getInteger(R.integer.max_puck_speed);
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