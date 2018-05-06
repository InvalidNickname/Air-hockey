package hockey.airhockey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_VOLUME = "volume";
    static int width, height, playerScale, puckScale, gateHeight, goalStopTime, startAnimStopTime, numberOfPucks, numberOfPlayers, goalThreshold;
    static double frictionValue;
    static float volume;
    static boolean friction;
    private ImageView volumeButton;
    private ImageView start, credits;
    private Animation left, right;
    private boolean isAnimation;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        playerScale = width / getResources().getInteger(R.integer.player_scale);
        puckScale = width / getResources().getInteger(R.integer.puck_scale);
        gateHeight = height / getResources().getInteger(R.integer.gate_height);
        friction = getResources().getBoolean(R.bool.friction);
        goalThreshold = getResources().getInteger(R.integer.goal_threshold);
        goalStopTime = getResources().getInteger(R.integer.goal_stop);
        startAnimStopTime = getResources().getInteger(R.integer.start_anim_stop);
        frictionValue = getResources().getInteger(R.integer.friction_value) / 1000d;
        numberOfPucks = getResources().getInteger(R.integer.number_of_pucks) - 1;
        numberOfPlayers = getResources().getInteger(R.integer.number_of_players) - 1;
        isAnimation = false;
        if (settings.contains(APP_PREFERENCES_VOLUME)) {
            volume = settings.getFloat(APP_PREFERENCES_VOLUME, 0);
        } else {
            volume = 1;
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);
        start = findViewById(R.id.start);
        credits = findViewById(R.id.credits);
        volumeButton = findViewById(R.id.volumeButton);
        if (volume == 0) {
            volumeButton.setBackground(VectorDrawableCompat.create(getResources(), R.drawable.volume_off, null));
        }
        left = AnimationUtils.loadAnimation(this, R.anim.go_left);
        right = AnimationUtils.loadAnimation(this, R.anim.go_right);
        drawGates();
    }

    private void drawGates() {
        ImageView upperGate = findViewById(R.id.upper_gate);
        ImageView lowerGate = findViewById(R.id.lower_gate);
        ConstraintLayout.LayoutParams upperParams = new ConstraintLayout.LayoutParams((int) (0.48 * width), gateHeight);
        upperParams.leftToLeft = R.id.main;
        upperParams.rightToRight = R.id.main;
        upperParams.topToTop = R.id.main;
        upperParams.bottomToBottom = R.id.main;
        upperParams.topMargin = -100;
        upperParams.verticalBias = 0;
        upperGate.setLayoutParams(upperParams);
        ConstraintLayout.LayoutParams lowerParams = new ConstraintLayout.LayoutParams((int) (0.48 * width), gateHeight);
        lowerParams.leftToLeft = R.id.main;
        lowerParams.rightToRight = R.id.main;
        lowerParams.topToTop = R.id.main;
        lowerParams.bottomToBottom = R.id.main;
        lowerParams.bottomMargin = -100;
        lowerParams.verticalBias = 1;
        lowerGate.setLayoutParams(lowerParams);
    }

    public void startGame(View view) {
        if (!isAnimation) {
            left.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    start.setVisibility(GONE);
                    credits.setVisibility(GONE);
                    Intent intent = new Intent(MainActivity.this, GameCustomActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            start.startAnimation(left);
            credits.startAnimation(right);
        }
    }

    public void openCredits(View view) {
        if (!isAnimation) {
            left.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    start.setVisibility(GONE);
                    credits.setVisibility(GONE);
                    Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            start.startAnimation(left);
            credits.startAnimation(right);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void changeVolume(View view) {
        if (volume == 1) {
            volume = 0;
            volumeButton.setBackground(VectorDrawableCompat.create(getResources(), R.drawable.volume_off, null));
        } else {
            volume = 1;
            volumeButton.setBackground(VectorDrawableCompat.create(getResources(), R.drawable.volume_up, null));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(APP_PREFERENCES_VOLUME, volume);
        editor.apply();
    }
}
