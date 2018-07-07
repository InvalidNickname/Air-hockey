package hockey.airhockey;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static hockey.airhockey.MainActivity.APP_PREFERENCES;
import static hockey.airhockey.MainActivity.HIDE_FLAGS;
import static hockey.airhockey.MainActivity.runningActivitiesCounter;
import static hockey.airhockey.MainActivity.settings;

public class SettingsActivity extends AppCompatActivity {

    static final String APP_PREFERENCES_MULTIPLAYER = "multiplayer";
    static final String APP_PREFERENCES_GAME_MODE = "game_mode";
    static final String APP_PREFERENCES_GOAL_THRESHOLD = "goal_threshold";
    static final String APP_PREFERENCES_GAME_LENGTH_TIME = "game_length_time";
    static final int GAME_MODE_POINTS = 1;
    static final int GAME_MODE_TIME = 2;

    private SharedPreferences preferences;
    private boolean multiplayer;
    private int gameMode, goalThreshold, gameLengthTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        overridePendingTransition(0, 0);
        drawGates();
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        multiplayer = preferences.getBoolean(APP_PREFERENCES_MULTIPLAYER, true);
        if (multiplayer) {
            selectGameModeMultiplayer();
        } else {
            selectGameModeSingleplayer();
        }
        gameMode = preferences.getInt(APP_PREFERENCES_GAME_MODE, GAME_MODE_POINTS);
        if (gameMode == GAME_MODE_TIME) {
            selectGameModeTime();
        } else if (gameMode == GAME_MODE_POINTS) {
            selectGameModePoints();
        }
        goalThreshold = preferences.getInt(APP_PREFERENCES_GOAL_THRESHOLD, 7);
        TextView points = findViewById(R.id.points);
        points.setText(String.valueOf(goalThreshold));
        gameLengthTime = preferences.getInt(APP_PREFERENCES_GAME_LENGTH_TIME, 2);
        TextView time = findViewById(R.id.time);
        time.setText(String.valueOf(gameLengthTime));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(APP_PREFERENCES_GAME_MODE, gameMode);
        editor.putBoolean(APP_PREFERENCES_MULTIPLAYER, multiplayer);
        editor.putInt(APP_PREFERENCES_GAME_LENGTH_TIME, gameLengthTime);
        editor.putInt(APP_PREFERENCES_GOAL_THRESHOLD, goalThreshold);
        editor.apply();
    }

    private void selectGameModePoints() {
        gameMode = GAME_MODE_POINTS;
        ConstraintLayout timeMode = findViewById(R.id.timeMode);
        timeMode.setBackground(null);
        ConstraintLayout pointsMode = findViewById(R.id.pointsMode);
        pointsMode.setBackground(getResources().getDrawable(R.drawable.stroke));
    }

    private void selectGameModeTime() {
        gameMode = GAME_MODE_TIME;
        ConstraintLayout timeMode = findViewById(R.id.timeMode);
        timeMode.setBackground(getResources().getDrawable(R.drawable.stroke));
        ConstraintLayout pointsMode = findViewById(R.id.pointsMode);
        pointsMode.setBackground(null);
    }

    private void selectGameModeSingleplayer() {
        multiplayer = false;
        ImageView singleplayerMode = findViewById(R.id.singleplayerMode);
        singleplayerMode.setBackground(getResources().getDrawable(R.drawable.stroke));
        ImageView multiplayerMode = findViewById(R.id.multiplayerMode);
        multiplayerMode.setBackground(null);
    }

    private void selectGameModeMultiplayer() {
        multiplayer = true;
        ImageView singleplayerMode = findViewById(R.id.singleplayerMode);
        singleplayerMode.setBackground(null);
        ImageView multiplayerMode = findViewById(R.id.multiplayerMode);
        multiplayerMode.setBackground(getResources().getDrawable(R.drawable.stroke));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pointsMode:
                selectGameModePoints();
                break;
            case R.id.timeMode:
                selectGameModeTime();
                break;
            case R.id.increasePoints:
                if (goalThreshold < 9) {
                    goalThreshold++;
                    TextView textView = findViewById(R.id.points);
                    textView.setText(String.valueOf(goalThreshold));
                }
                break;
            case R.id.decreasePoints:
                if (goalThreshold > 1) {
                    goalThreshold -= 1;
                    TextView textView = findViewById(R.id.points);
                    textView.setText(String.valueOf(goalThreshold));
                }
                break;
            case R.id.increaseTime:
                if (gameLengthTime < 5) {
                    gameLengthTime++;
                    TextView textView = findViewById(R.id.time);
                    textView.setText(String.valueOf(gameLengthTime));
                }
                break;
            case R.id.decreaseTime:
                if (gameLengthTime > 1) {
                    gameLengthTime -= 1;
                    TextView textView = findViewById(R.id.time);
                    textView.setText(String.valueOf(gameLengthTime));
                }
                break;
            case R.id.singleplayerMode:
                selectGameModeSingleplayer();
                break;
            case R.id.multiplayerMode:
                selectGameModeMultiplayer();
                break;
            case R.id.apply:
                finish();
                break;
        }
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


    @Override
    protected void onStop() {
        super.onStop();
        runningActivitiesCounter -= 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        runningActivitiesCounter++;
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(HIDE_FLAGS);
    }
}
