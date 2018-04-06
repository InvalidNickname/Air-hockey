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

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity {

    Button start, settings;
    Animation up, down;
    View upperGate, lowerGate;
    ConstraintLayout main;
    static int width, height, playerScale, puckScale, frictionValue, gateHeight, goalStopTime;
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
        goalStopTime = getResources().getInteger(R.integer.goal_stop);
        frictionValue = getResources().getInteger(R.integer.friction_value);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        settings = findViewById(R.id.settings);
        main = findViewById(R.id.main);
        up = AnimationUtils.loadAnimation(this, R.anim.go_up);
        down = AnimationUtils.loadAnimation(this, R.anim.go_down);
        drawGates();
    }

    private void drawGates() {
        upperGate = new View(this);
        lowerGate = new View(this);
        upperGate.setBackground(getResources().getDrawable(R.drawable.upper_gate));
        lowerGate.setBackground(getResources().getDrawable(R.drawable.lower_gate));
        ConstraintLayout.LayoutParams upperParams = new ConstraintLayout.LayoutParams(width / 2, gateHeight);
        upperParams.leftToLeft = R.id.main;
        upperParams.rightToRight = R.id.main;
        upperParams.topToTop = R.id.main;
        upperParams.bottomToBottom = R.id.main;
        upperParams.topMargin = -100;
        upperParams.verticalBias = 0;
        upperGate.setLayoutParams(upperParams);
        main.addView(upperGate);
        ConstraintLayout.LayoutParams lowerParams = new ConstraintLayout.LayoutParams(width / 2, gateHeight);
        lowerParams.leftToLeft = R.id.main;
        lowerParams.rightToRight = R.id.main;
        lowerParams.topToTop = R.id.main;
        lowerParams.bottomToBottom = R.id.main;
        lowerParams.bottomMargin = -100;
        lowerParams.verticalBias = 1;
        lowerGate.setLayoutParams(lowerParams);
        main.addView(lowerGate);
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
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
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
}
