package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.friction;
import static hockey.airhockey.MainActivity.frictionValue;
import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.puckScale;
import static hockey.airhockey.MainActivity.width;

class Puck {

    int x, y;
    Vector v;
    private VectorDrawableCompat drawable;

    Puck(int resId, Context context) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        v = new Vector(0, 0);
        x = width / 2;
        y = height / 2;
        if (drawable != null) {
            drawable.setBounds(x - puckScale, y - puckScale, x + puckScale, y + puckScale);
        }
    }

    void update(long sec, long psec) {
        v.setVector(v.x, v.y);
        x += v.x * (sec - psec);
        y += v.y * (sec - psec);
        if (friction) {
            v = v.multiplyVector(frictionValue);
        }
        drawable.setBounds(x - puckScale, y - puckScale, x + puckScale, y + puckScale);
    }

    void drawPuck(Canvas canvas) {
        drawable.draw(canvas);
    }
}
