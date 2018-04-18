package hockey.airhockey;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity {

    Button start, settings;
    Animation up, down;
    static int width, height, playerScale, puckScale, gateHeight, goalStopTime, startAnimStopTime, numberOfPucks, numberOfPlayers, goalThreshold;
    static double frictionValue;
    static boolean friction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        playerScale = getResources().getDisplayMetrics().widthPixels / getResources().getInteger(R.integer.player_scale);
        puckScale = getResources().getDisplayMetrics().widthPixels / getResources().getInteger(R.integer.puck_scale);
        gateHeight = getResources().getDisplayMetrics().widthPixels / getResources().getInteger(R.integer.gate_height);
        friction = getResources().getBoolean(R.bool.friction);
        goalThreshold = getResources().getInteger(R.integer.goal_threshold);
        goalStopTime = getResources().getInteger(R.integer.goal_stop);
        startAnimStopTime = getResources().getInteger(R.integer.start_anim_stop);
        frictionValue = getResources().getInteger(R.integer.friction_value) / 1000d;
        numberOfPucks = getResources().getInteger(R.integer.number_of_pucks) - 1;
        numberOfPlayers = getResources().getInteger(R.integer.number_of_players) - 1;
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);
        start = findViewById(R.id.start);
        settings = findViewById(R.id.settings);
        up = AnimationUtils.loadAnimation(this, R.anim.go_up);
        down = AnimationUtils.loadAnimation(this, R.anim.go_down);
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
        up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                start.setVisibility(GONE);
                settings.setVisibility(GONE);
                Intent intent = new Intent(MainActivity.this, GameCustomActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        start.startAnimation(up);
        settings.startAnimation(down);
    }

    public void openSettings(View view) {
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
