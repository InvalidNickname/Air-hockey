package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.settings;

class Puck {

    private final VectorDrawableCompat drawable;
    double x, y;
    Vector v;
    private long psec;

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
        psec = System.currentTimeMillis();
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

    void update(double delta, boolean isAnimation) {
        if (!isAnimation) {
            v.setVector(v.x, v.y);
            if (v.v * delta > settings.playerScale * 2) {
                v.setVector(settings.playerScale * 2 * v.cos / (delta + .0), settings.playerScale * 2 * v.sin / (delta + .0));
            }
        }
        x += v.x * delta;
        y += v.y * delta;
        if (settings.friction & !isAnimation & System.currentTimeMillis() - psec >= 10) {
            v = v.multiplyVector(settings.frictionValue);
            psec = System.currentTimeMillis();
        }
        drawable.setBounds((int) x - settings.puckScale, (int) y - settings.puckScale, (int) x + settings.puckScale, (int) y + settings.puckScale);
    }

    void draw(Canvas canvas) {
        drawable.draw(canvas);
    }
}
