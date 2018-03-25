package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.GameActivity.friction;
import static hockey.airhockey.GameActivity.frictionValue;
import static hockey.airhockey.GameActivity.height;
import static hockey.airhockey.GameActivity.playerScale;
import static hockey.airhockey.GameActivity.puckScale;
import static hockey.airhockey.GameActivity.width;

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

    void update(long delta) {
        v.setVector(v.x, v.y);
        x += v.x * delta;
        y += v.y * delta;
        if (friction) {
            v = v.multiplyVector(frictionValue);
        }
        if (v.v > playerScale * 2) {
            v.setVector((playerScale * 2) * Math.cos(v.angle), (playerScale * 2 * delta) * Math.sin(v.angle));
        }
        drawable.setBounds(x - puckScale, y - puckScale, x + puckScale, y + puckScale);
    }

    void drawPuck(Canvas canvas) {
        drawable.draw(canvas);
    }
}
