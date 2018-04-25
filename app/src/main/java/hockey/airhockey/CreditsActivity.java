package hockey.airhockey;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import static hockey.airhockey.MainActivity.gateHeight;
import static hockey.airhockey.MainActivity.width;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        overridePendingTransition(0, 0);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        drawGates();
        TextView versionText = findViewById(R.id.versionText);
        String versionName = "0.0";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionText.setText(String.format(getResources().getString(R.string.about_version), versionName));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreditsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void drawGates() {
        ImageView upperGate = findViewById(R.id.upper_gate);
        ImageView lowerGate = findViewById(R.id.lower_gate);
        ConstraintLayout.LayoutParams upperParams = new ConstraintLayout.LayoutParams((int) (0.48 * width), gateHeight);
        upperParams.leftToLeft = R.id.main_credits;
        upperParams.rightToRight = R.id.main_credits;
        upperParams.topToTop = R.id.main_credits;
        upperParams.bottomToBottom = R.id.main_credits;
        upperParams.topMargin = -100;
        upperParams.verticalBias = 0;
        upperGate.setLayoutParams(upperParams);
        ConstraintLayout.LayoutParams lowerParams = new ConstraintLayout.LayoutParams((int) (0.48 * width), gateHeight);
        lowerParams.leftToLeft = R.id.main_credits;
        lowerParams.rightToRight = R.id.main_credits;
        lowerParams.topToTop = R.id.main_credits;
        lowerParams.bottomToBottom = R.id.main_credits;
        lowerParams.bottomMargin = -100;
        lowerParams.verticalBias = 1;
        lowerGate.setLayoutParams(lowerParams);
    }

    public void back(View view) {
        onBackPressed();
    }
}
