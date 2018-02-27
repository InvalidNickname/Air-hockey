package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.puckScale;
import static hockey.airhockey.MainActivity.width;

class Puck {

    int x, y, mass;
    double vX, vY;
    private VectorDrawableCompat drawable;

    Puck(int resId, Context context) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        mass = context.getResources().getInteger(R.integer.puck_mass);
        vX = 0;
        vY = 0;
        x = width / 2;
        y = height / 2;
        if (drawable != null) {
            drawable.setBounds(x - puckScale, y - puckScale, x + puckScale, y + puckScale);
        }
    }

    void update(long sec, long psec) {
        if (vX > 5) {
            vX = 5;
        }
        if (vY > 5) {
            vY = 5;
        }
        if (vX < -5) {
            vX = -5;
        }
        if (vY < -5) {
            vY = -5;
        }
        x += vX * (sec - psec);
        y += vY * (sec - psec);
        drawable.setBounds(x - puckScale, y - puckScale, x + puckScale, y + puckScale);
    }

    void drawPuck(Canvas canvas) {
        drawable.draw(canvas);
    }
}
