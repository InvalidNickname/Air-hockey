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

    double x, y;
    Vector v;
    private VectorDrawableCompat drawable;

    Puck(int resId, Context context) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        v = new Vector(0, 0);
        x = width / 2;
        y = height / 2;
        if (drawable != null) {
            drawable.setBounds((int) x - puckScale, (int) y - puckScale, (int) x + puckScale, (int) y + puckScale);
        }
    }

    void update(long delta, boolean isAnimation) {
        if (!isAnimation) {
            v.setVector(v.x, v.y);
        }
        x += v.x * delta;
        y += v.y * delta;
        if (friction & !isAnimation) {
            v = v.multiplyVector(frictionValue);
        }
        drawable.setBounds((int) x - puckScale, (int) y - puckScale, (int) x + puckScale, (int) y + puckScale);
    }

    void draw(Canvas canvas) {
        drawable.draw(canvas);
    }
}
