package hockey.airhockey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WinActivity extends AppCompatActivity {

    ImageView upperBlocker, lowerBlocker;
    Animation fade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(0, 0);
        upperBlocker = findViewById(R.id.upperBlocker);
        lowerBlocker = findViewById(R.id.lowerBlocker);
        Intent intent = getIntent();
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        if (intent.getIntExtra("winner", 0) == 1) {
            upperBlocker.setImageAlpha(0);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    lowerBlocker.setImageAlpha(255);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            lowerBlocker.startAnimation(fade);
        } else {
            lowerBlocker.setImageAlpha(0);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    upperBlocker.setImageAlpha(255);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            upperBlocker.startAnimation(fade);
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void goToMenu(View view) {
        Intent intent = new Intent(WinActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void restartGame(View view) {
    }
}