package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.settings;

class Puck {

    private final VectorDrawableCompat drawable;
    double x, y;
    Vector v;

    Puck(int resId, Context context, long num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        v = new Vector(0, 0);
        if (num == 1) {
            y = settings.height / 3;
        } else if (num == 2) {
            y = settings.height * (2 / 3d);
        }
        x = settings.width / 2;
        if (drawable != null) {
            drawable.setBounds((int) x - settings.puckScale, (int) y - settings.puckScale, (int) x + settings.puckScale, (int) y + settings.puckScale);
        }
    }

    Puck(int resId, Context context) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        v = new Vector(0, 0);
        y = settings.height / 2;
        x = settings.width / 2;
        if (drawable != null) {
            drawable.setBounds((int) x - settings.puckScale, (int) y - settings.puckScale, (int) x + settings.puckScale, (int) y + settings.puckScale);
        }
    }

    void update(long delta, boolean isAnimation) {
        if (!isAnimation) {
            v.setVector(v.x, v.y);
            if (v.v * delta > settings.playerScale * 2) {
                v.setVector(settings.playerScale * 2 * v.cos / delta, settings.playerScale * 2 * v.sin / delta);
            }
        }
        x += v.x * delta;
        y += v.y * delta;
        if (settings.friction & !isAnimation) {
            v = v.multiplyVector(settings.frictionValue);
        }
        drawable.setBounds((int) x - settings.puckScale, (int) y - settings.puckScale, (int) x + settings.puckScale, (int) y + settings.puckScale);
    }

    void draw(Canvas canvas) {
        drawable.draw(canvas);
    }
}
